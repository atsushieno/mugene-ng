package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.Midi1Event
import dev.atsushieno.ktmidi.Midi1Music
import dev.atsushieno.ktmidi.Midi1WriterExtension
import dev.atsushieno.ktmidi.convertDeltaTimesToJRTimestamps
import dev.atsushieno.ktmidi.write
import kotlin.js.ExperimentalJsExport
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

@OptIn(ExperimentalJsExport::class)
//@JsExport
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

    @JsExport.Ignore
    fun compile(skipDefaultMmlFiles: Boolean, vararg mmlParts: String): Midi1Music {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", mml) }.toTypedArray()
        return compile(skipDefaultMmlFiles, inputs = sources)
    }

    @JsName("compile")
    @JvmName("doNotUseCompile")
    fun compile(skipDefaultMmlFiles: Boolean, inputs: Array<MmlInputSource>) = generateMusic(buildSemanticTree(tokenizeInputs(false, skipDefaultMmlFiles, inputs.toList())))

    @JsExport.Ignore
    fun compile(skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) = generateMusic(buildSemanticTree(tokenizeInputs(false, skipDefaultMmlFiles, inputs.toList())))

    @JsExport.Ignore
    fun compile(skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>, metaWriter: ((Boolean, Midi1Event, MutableList<Byte>) -> Int)?, output: MutableList<Byte>, disableRunningStatus: Boolean) {
        val music = compile(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        music.write(output, metaWriter ?: Midi1WriterExtension.defaultMetaEventWriter, disableRunningStatus)
    }

    @JsExport.Ignore
    fun compile2(skipDefaultMmlFiles: Boolean, vararg mmlParts: String): Midi2Music =
        compile2(skipDefaultMmlFiles, mmlParts.map { mml -> MmlInputSource("<string>", mml) }.toTypedArray())

    @JsName("compile2")
    @JvmName("doNotUseCompile2a")
    fun compile2(skipDefaultMmlFiles: Boolean, inputs: Array<MmlInputSource>) =
        generateMusic2(buildSemanticTree(tokenizeInputs(true, skipDefaultMmlFiles, inputs.toList())))

    @JsExport.Ignore
    fun compile2(skipDefaultMmlFiles: Boolean, vararg inputs: MmlInputSource) =
        generateMusic2(buildSemanticTree(tokenizeInputs(true, skipDefaultMmlFiles, inputs.toList())))

    @JsExport.Ignore
    fun compile2(skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>, output: MutableList<Byte>) {
        val music = compile2(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        music.write(output)
    }

    // FIXME: make it public maybe, but once all those @JsExport issues are resolved (or maybe we should wait for Kotlin/Wasm)
    // used by language server and compiler.
    private fun tokenizeInputs(isMidi2: Boolean, skipDefaultMmlFiles: Boolean, inputs: List<MmlInputSource>): MmlTokenSet {
        val defaults = if (isMidi2) Util.defaultIncludes2 else Util.defaultIncludes
        val actualInputs =
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

    private fun generateMusic(tree: MmlSemanticTreeSet): Midi1Music {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, this)
        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, this, false)
        // raw events -> SMF
        return MmlSmfGenerator.generate(resolved)
    }

    private fun generateMusic2(tree: MmlSemanticTreeSet): Midi2Music {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, this)
        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, this, true)
        // raw events -> UMP music format
        return MmlMidi2Generator.generate(resolved)
    }

    init {
        report = { v, k, m -> reportOnConsole(v, k, m) }
    }
}

