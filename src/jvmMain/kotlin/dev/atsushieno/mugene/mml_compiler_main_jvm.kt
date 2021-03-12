package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriter
import dev.atsushieno.ktmidi.SmfWriterExtension
import java.io.*
import java.nio.charset.Charset

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

class MmlCompiler() {
    companion object {
        val defaultIncludes = Util.defaultIncludes
    }

    var verbose = false

    var resolver: StreamResolver =
        MergeStreamResolver(LocalFileStreamResolver())

    var report: MmlDiagnosticReporter

    var continueOnError = false

    fun reportOnConsole(
        verbosity: MmlDiagnosticVerbosity,
        location: MmlLineInfo?,
        format: String,
        vararg args: Any
    ) {
        val kind =
            if (verbosity == MmlDiagnosticVerbosity.Error) "error"
            else if (verbosity == MmlDiagnosticVerbosity.Warning) "warning"
            else "information";
        val loc =
            if (location != null) "$location.file $location.lineNumber, $location.linePosition) : " else ""
        val msg = if (args.any()) String.format(format, args) else format
        val output = "$loc$kind: $msg"
        if (verbosity != MmlDiagnosticVerbosity.Error || continueOnError)
            println(output)
        else
            throw MmlException(output, null)
    }

    fun compile(args: List<String>) {
        try {
            compileCore(args)
        } catch (ex: MmlException) {
            System.err.println(ex.message)
        } catch (ex: Exception) {
            if (verbose)
                throw ex
            System.err.println(ex.toString())
        }
    }

    private val help = """MML compiler mugene

Usage: mugene [options] mml_files

Options:
--output
specify explicit output file name.
--verbose
prints debugging aid.
--disable-running-status
disables running status in SMF.
--encoding
uses specified encoding.
Shift_JIS, euc-jp, iso-2022-jp etc.
--nodefault
prevents default mml files being included.
This option is for core MML operation hackers."""

    fun compileCore(args: List<String>) {

        if (!args.any()) {
            report(MmlDiagnosticVerbosity.Error, null, help, listOf())
            return
        }

        // file names -> input sources
        val inputFilenames = mutableListOf<String>()
        var outFilename: String? = null
        var explicitFilename: String? = null
        var disableRunningStatus = false
        val extension = ".mid"
        val metaWriter = SmfWriterExtension.DEFAULT_META_EVENT_WRITER
        var noDefault = false

        for (arg in args) {
            when (arg) {
                "--nodefault" -> {
                    noDefault = true
                    continue
                }
                "--verbose" -> {
                    verbose = true
                    continue
                }
                "--disable-running-status" -> {
                    disableRunningStatus = true
                    continue
                }
                else -> {
                    if (arg.startsWith("--encoding:")) {
                        val enc = arg.substring(11)
                        MmlValueExprResolver.stringToBytes =
                            { s -> s.toByteArray(Charset.forName(enc)) }
                        continue
                    }
                    if (arg.startsWith("--output:")) {
                        explicitFilename = arg.substring(9);
                        continue;
                    }
                    if (arg == "--help") {
                        report(MmlDiagnosticVerbosity.Error, null, help, null)
                        return
                    }
                }
            }
            val argAsFile = File(arg)
            outFilename = argAsFile.nameWithoutExtension + extension
            inputFilenames.add(arg);
        }
        if (explicitFilename != null)
            outFilename = explicitFilename;

        // FIXME: stream resolver should be processed within the actual parsing phase.
        // This makes it redundant to support #include
        val inputs = mutableListOf<MmlInputSource>()
        for (fname in inputFilenames)
            inputs.add(MmlInputSource(fname, resolver.getEntity(fname)));

        FileOutputStream(outFilename).use {
            compile(noDefault, inputs, metaWriter, it, disableRunningStatus);
        }
        report(
            MmlDiagnosticVerbosity.Information,
            null,
            "Written SMF file ... $outFilename",
            listOf()
        )
    }

    class MmlCompilerOptions {
        var skipDefaultMmlFiles = false
        var disableRunningStatus = false
        var metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)? = null
    }

    fun compile(
        skipDefaultMmlFiles: Boolean,
        inputs: List<MmlInputSource>,
        metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)?,
        output: OutputStream,
        disableRunningStatus: Boolean
    ) {
        val music = compile(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        music.save(output, disableRunningStatus, metaWriter)
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
        val tokenizerSources = MmlInputSourceReader.parse(report, resolver, inputs);

        // tokenizer sources -> token streams
        return MmlTokenizer.tokenize(report, tokenizerSources);
    }

    // used by language server and compiler.
    private fun buildSemanticTree(tokens: MmlTokenSet): MmlSemanticTreeSet {
        // token streams -> semantic trees
        return MmlSemanticTreeBuilder.compile(tokens, report);
    }

    private fun generateMusic(tree: MmlSemanticTreeSet): MidiMusic {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, report);

        // simplified streams -> raw events
        val resolved = MmlEventStreamGenerator.generate(tree, report);

        // raw events -> SMF
        val smf = MmlSmfGenerator.generate(resolved);

        return smf;
    }

    init {
        report = { v, k, f, a -> reportOnConsole(v, k, f, (a ?: listOf()).toTypedArray()) }
    }
}

fun MidiMusic.save(
    output: OutputStream,
    disableRunningStatus: Boolean = false,
    metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)? = null
) {
    val writer = SmfWriter(output).apply { this.disableRunningStatus = disableRunningStatus };
    if (metaWriter != null)
        writer.metaEventWriter = metaWriter!!
    writer.writeMusic(this);
}

fun MidiMusic.toBytes(
    disableRunningStatus: Boolean = false,
    metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)? = null
): Array<Byte> {
    val ms = ByteArrayOutputStream();
    this.save(ms, disableRunningStatus, metaWriter);
    return ms.toByteArray().toTypedArray()
}
