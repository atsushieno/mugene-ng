package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriterExtension
import dev.atsushieno.ktmidi.convertDeltaTimesToJRTimestamps
import dev.atsushieno.ktmidi.write
import kotlin.js.JsExport
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

@JsExport
abstract class MmlCompiler {
    companion object {
        fun create(): MmlCompiler = createDefaultCompiler()
    }

    abstract fun decodeStringUsingEncoding(s: String, charset: String = "utf-8"): ByteArray

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

    @JsName("doNotUseCompile_0")
    fun compile(skipDefaultMmlFiles: Boolean, vararg mmlParts: String): MidiMusic {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", mml) }.toTypedArray()
        return compile(skipDefaultMmlFiles, inputs = sources)
    }

    @JsName("compile")
    @JvmName("doNotUseCompile")
    fun compile(skipDefaultMmlFiles: Boolean, inputs: Array<MmlInputSource>) = generateMusic(buildSemanticTree(tokenizeInputs(false, skipDefaultMmlFiles, inputs.toList())))

    @JsName("doNotUseCompile_1")
    fun compile(skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) = generateMusic(buildSemanticTree(tokenizeInputs(false, skipDefaultMmlFiles, inputs.toList())))

    @JsName("doNotUseCompile_2")
    fun compile(skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>, metaWriter: ((Boolean, MidiMessage, MutableList<Byte>) -> Int)?, output: MutableList<Byte>, disableRunningStatus: Boolean) {
        val music = compile(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        music.write(output, metaWriter ?: SmfWriterExtension.DEFAULT_META_EVENT_WRITER, disableRunningStatus)
    }

    @JsName("doNotUseCompile2_0")
    fun compile2(outputDeltaTime: Boolean, skipDefaultMmlFiles: Boolean, vararg mmlParts: String): Midi2Music {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", mml) }.toTypedArray()
        return compile2(outputDeltaTime, skipDefaultMmlFiles, inputs = sources)
    }

    @JsName("compile2")
    @JvmName("doNotUseCompile2")
    fun compile2(outputDeltaTime: Boolean, skipDefaultMmlFiles: Boolean, inputs: Array<MmlInputSource>) =
        generateMusic2(outputDeltaTime, buildSemanticTree(tokenizeInputs(true, skipDefaultMmlFiles, inputs.toList())))

    @JsName("doNotUseCompile2_1")
    fun compile2(outputDeltaTime: Boolean, skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) =
        generateMusic2(outputDeltaTime, buildSemanticTree(tokenizeInputs(true, skipDefaultMmlFiles, inputs.toList())))

    @JsName("doNotUseCompile2_2")
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
        val tokenizerSources = MmlInputSourceReader.parse(this, resolver, actualInputs.toMutableList())

        // tokenizer sources -> token streams
        return MmlTokenizer.tokenize(report, tokenizerSources)
    }

    // used by language server and compiler.
    private fun buildSemanticTree(tokens: MmlTokenSet): MmlSemanticTreeSet {
        // token streams -> semantic trees
        return MmlSemanticTreeBuilder.compile(tokens, this)
    }

    private fun generateMusic(tree: MmlSemanticTreeSet): MidiMusic {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, this)
        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, this, false)
        // raw events -> SMF
        return MmlSmfGenerator.generate(resolved)
    }

    private fun generateMusic2(outputDeltaTime: Boolean, tree: MmlSemanticTreeSet): Midi2Music {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, this)
        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, this, true)
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

