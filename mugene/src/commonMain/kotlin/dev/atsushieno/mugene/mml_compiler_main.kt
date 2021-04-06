package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.Midi2MusicWriter
import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriter

internal class Util {
    companion object {
        val defaultIncludes = listOf(
            "default-macro.mml",
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

abstract class MmlCompiler {
    companion object {
        val defaultIncludes = Util.defaultIncludes
    }

    var verbose = false

    abstract var resolver: StreamResolver
    var report: MmlDiagnosticReporter

    private var continueOnError = false

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

    fun compile(skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) = generateMusic(buildSemanticTree(tokenizeInputs(skipDefaultMmlFiles, inputs.toList())))

    fun compile(skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>, metaWriter: ((Boolean, MidiMessage, MutableList<Byte>) -> Int)?, output: MutableList<Byte>, disableRunningStatus: Boolean) {
        val music = compile(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        val writer = SmfWriter(output).apply {
            this.disableRunningStatus = disableRunningStatus
            if (metaWriter != null)
                this.metaEventWriter = metaWriter
        }
        writer.writeMusic(music)
    }

    fun compile2(skipDefaultMmlFiles: Boolean, vararg mmlParts: String): Midi2Music {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", mml) }.toTypedArray()
        return compile2(skipDefaultMmlFiles, inputs = sources)
    }

    fun compile2(skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) = generateMusic2(buildSemanticTree(tokenizeInputs(skipDefaultMmlFiles, inputs.toList())))

    fun compile2(skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>, output: MutableList<Byte>) {
        val music = compile2(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        Midi2MusicWriter(output).writeMusic(music)
    }

    // used by language server and compiler.
    fun tokenizeInputs(skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>): MmlTokenSet {
        var actualInputs =
            if (!skipDefaultMmlFiles)
                Util.defaultIncludes.map { f ->
                    MmlInputSource(
                        f,
                        resolver.getEntity(f)
                    )
                } + inputs
            else inputs

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

    private fun generateMusic2(tree: MmlSemanticTreeSet): Midi2Music {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, report)
        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, report)
        // raw events -> SMF
        return MmlMidi2Generator.generate(resolved)
    }

    init {
        report = { v, k, m -> reportOnConsole(v, k, m) }
    }
}
