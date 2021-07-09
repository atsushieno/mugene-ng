package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriter
import dev.atsushieno.ktmidi.convertDeltaTimesToJRTimestamps
import dev.atsushieno.ktmidi.write
import kotlin.js.JsName
import kotlin.jvm.JvmName

internal class Util {
    companion object {
        val defaultIncludes = listOf(
            "default-macro.mml",
            "drum-part.mml",
            "gs-sysex.mml",
            "nrpn-gs-xg.mml",
        )
        val defaultIncludes2 = listOf(
            "default-macro2.mml",
            "drum-part.mml",
            "gs-sysex.mml",
            "nrpn-gs-xg.mml",
        )
    }
}

enum class MmlDiagnosticVerbosity {
    Error,
    Warning,
    Information,
}

typealias MmlDiagnosticReporter = (verbosity: MmlDiagnosticVerbosity, location: MmlLineInfo?, message: String) -> Unit

internal expect fun createDefaultCompiler(): MmlCompiler

abstract class MmlCompiler {
    companion object {
        fun create(): MmlCompiler = createDefaultCompiler()
    }

    var verbose = false

    abstract var resolver: StreamResolver
    var report: MmlDiagnosticReporter

    var continueOnError = false

    private fun reportOnConsole(
        verbosity: MmlDiagnosticVerbosity,
        location: MmlLineInfo?,
        message: String) {
        val kind =
            if (verbosity == MmlDiagnosticVerbosity.Error) "error"
            else if (verbosity == MmlDiagnosticVerbosity.Warning) "warning"
            else "information"
        val loc =
            if (location != null) "${location.file} (${location.lineNumber}, ${location.linePosition}) : " else ""
        val output = "$loc$kind: $message"
        if (verbosity != MmlDiagnosticVerbosity.Error || continueOnError)
            println(output)
        else
            throw MmlException(output, null)
    }

    fun compile(skipDefaultMmlFiles: Boolean, vararg mmlParts: String): MidiMusic {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", mml) }.toTypedArray()
        return compile(skipDefaultMmlFiles, inputs = sources)
    }

    @JsName("compile")
    @JvmName("doNotUseCompile")
    fun compile(skipDefaultMmlFiles: Boolean, inputs: Array<MmlInputSource>) = generateMusic(buildSemanticTree(tokenizeInputs(false, skipDefaultMmlFiles, inputs.toList())))

    fun compile(skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) = generateMusic(buildSemanticTree(tokenizeInputs(false, skipDefaultMmlFiles, inputs.toList())))

    fun compile(skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>, metaWriter: ((Boolean, MidiMessage, MutableList<Byte>) -> Int)?, output: MutableList<Byte>, disableRunningStatus: Boolean) {
        val music = compile(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        val writer = SmfWriter(output).apply {
            this.disableRunningStatus = disableRunningStatus
            if (metaWriter != null)
                this.metaEventWriter = metaWriter
        }
        writer.writeMusic(music)
    }

    fun compile2(outputDeltaTime: Boolean, skipDefaultMmlFiles: Boolean, vararg mmlParts: String): Midi2Music {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", mml) }.toTypedArray()
        return compile2(outputDeltaTime, skipDefaultMmlFiles, inputs = sources)
    }

    @JsName("compile2")
    @JvmName("doNotUseCompile2")
    fun compile2(outputDeltaTime: Boolean, skipDefaultMmlFiles: Boolean, inputs: Array<MmlInputSource>) =
        generateMusic2(outputDeltaTime, buildSemanticTree(tokenizeInputs(true, skipDefaultMmlFiles, inputs.toList())))

    fun compile2(outputDeltaTime: Boolean, skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) =
        generateMusic2(outputDeltaTime, buildSemanticTree(tokenizeInputs(true, skipDefaultMmlFiles, inputs.toList())))

    fun compile2(outputDeltaTime: Boolean, skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>, output: MutableList<Byte>) {
        val music = compile2(outputDeltaTime, skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        music.write(output)
    }

    // used by language server and compiler.
    fun tokenizeInputs(isMidi2: Boolean, skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>): MmlTokenSet {
        val defaults = if (isMidi2) Util.defaultIncludes2 else Util.defaultIncludes
        var actualInputs =
            if (!skipDefaultMmlFiles)
                defaults.map { f -> MmlInputSource(f, resolver.getEntity(f)) } + inputs
            else
                inputs

        // input sources -> tokenizer sources
        val tokenizerSources = MmlInputSourceReader.parse(report, resolver, actualInputs.toMutableList())

        // tokenizer sources -> token streams
        return MmlTokenizer.tokenize(report, tokenizerSources)
    }

    // used by language server and compiler.
    private fun buildSemanticTree(tokens: MmlTokenSet): MmlSemanticTreeSet {
        // token streams -> semantic trees
        return MmlSemanticTreeBuilder.compile(tokens, report)
    }

    private fun generateMusic(tree: MmlSemanticTreeSet): MidiMusic {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, report)
        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, report)
        // raw events -> SMF
        return MmlSmfGenerator.generate(resolved)
    }

    private fun generateMusic2(outputDeltaTime: Boolean, tree: MmlSemanticTreeSet): Midi2Music {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, report)
        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, report)
        // raw events -> UMP music format
        val umpmf = MmlMidi2Generator.generate(resolved)
        if (outputDeltaTime)
            return umpmf
        // convert DeltaTimes to JR Timestamps (which UMP players can directly play each track)
        return umpmf.convertDeltaTimesToJRTimestamps()
    }

    init {
        report = { v, k, m -> reportOnConsole(v, k, m) }
    }
}

