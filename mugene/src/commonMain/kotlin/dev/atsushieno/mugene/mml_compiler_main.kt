package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic

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
            if (location != null) "$location.file $location.lineNumber, $location.linePosition) : " else ""
        val output = "$loc$kind: $message"
        if (verbosity != MmlDiagnosticVerbosity.Error || continueOnError)
            println(output)
        else
            throw MmlException(output, null)
    }

    fun compile(skipDefaultMmlFiles: Boolean, vararg mmlParts: String): MidiMusic {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", mml) }
        return compile(skipDefaultMmlFiles, inputs = sources.toTypedArray())
    }

    fun compile(skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource): MidiMusic {
        return generateMusic(
            buildSemanticTree(
                tokenizeInputs(
                    skipDefaultMmlFiles,
                    inputs.toList()
                )
            )
        )
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
        val smf = MmlSmfGenerator.generate(resolved)

        return smf
    }

    init {
        report = { v, k, m -> reportOnConsole(v, k, m) }
    }
}
