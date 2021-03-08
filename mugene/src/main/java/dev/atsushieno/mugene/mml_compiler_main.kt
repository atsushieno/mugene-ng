package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriter
import dev.atsushieno.ktmidi.SmfWriterExtension
import java.io.*
import java.nio.charset.Charset
import java.util.stream.Stream

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

typealias MmlDiagnosticReporter = (verbosity: MmlDiagnosticVerbosity, location: MmlLineInfo?, format: String, args: List<Any>?) -> Unit

class MmlCompiler {
    companion object {
        val defaultIncludes = Util.defaultIncludes
    }

    constructor() {
        report = { v, k, f, a -> reportOnConsole(v, k, f, (a ?: listOf()).toTypedArray()) }
    }

    var verbose = false

    var resolver: StreamResolver =
        MergeStreamResolver(LocalFileStreamResolver(), AndroidAssetStreamResolver())

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

    val help = """MML compiler mugene

Usage: mugene [options] mml_files

Options:
--output
specify explicit output file name.
--vsq
Vocaloid VSQ mode on.
Changes extension to .vsq and encoding to ShiftJIS,
and uses VSQ metadata mode.
--uvsq
Vocaloid VSQ mode on.
and uses VSQ metadata mode.
No changes on encoding, to support Bopomofo.
--verbose
prints debugging aid.
--use-vsq-metadata
uses Vocaloid VSQ metadata mode.
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
        var inputFilenames = mutableListOf<String>()
        var outfilename: String? = null
        var explicitfilename: String? = null
        var disableRunningStatus = false
        var extension = ".mid"
        var metaWriter = SmfWriterExtension.DEFAULT_META_EVENT_WRITER
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
                        var enc = arg.substring(11)
                        MmlValueExprResolver.stringToBytes =
                            { s -> s.toByteArray(Charset.forName(enc)) }
                        continue
                    }
                    if (arg.startsWith("--output:")) {
                        explicitfilename = arg.substring(9);
                        continue;
                    }
                    if (arg == "--help") {
                        report(MmlDiagnosticVerbosity.Error, null, help, null)
                        return
                    }
                }
            }
            var argAsFile = File(arg)
            outfilename = argAsFile.nameWithoutExtension + extension
            inputFilenames.add(arg);
        }
        if (explicitfilename != null)
            outfilename = explicitfilename;

        // FIXME: stream resolver should be processed within the actual parsing phase.
        // This makes it redundant to support #include
        var inputs = mutableListOf<MmlInputSource>()
        for (fname in inputFilenames)
            inputs.add(MmlInputSource(fname, resolver.getEntity(fname)));

        FileOutputStream(outfilename).use {
            compile(noDefault, inputs, metaWriter, it, disableRunningStatus);
        }
        report(
            MmlDiagnosticVerbosity.Information,
            null,
            "Written SMF file ... $outfilename",
            listOf()
        )
    }

    class MmlCompilerOptions {
        var skipDefaultMmlFiles = false
        var disableRunningStatus = false
        var useVsqMetadata = false
        var metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)? = null
    }

    fun compile(
        skipDefaultMmlFiles: Boolean,
        inputs: List<MmlInputSource>,
        metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)?,
        output: OutputStream,
        disableRunningStatus: Boolean
    ) {
        var music = compile(skipDefaultMmlFiles, inputs = inputs.toTypedArray())
        music.save(output, disableRunningStatus, metaWriter)
    }

    fun compile(skipDefaultMmlFiles: Boolean, vararg mmlParts: String): MidiMusic {
        val sources = mmlParts.map { mml -> MmlInputSource("<string>", StringReader(mml)) }
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
        var tokenizerSources = MmlInputSourceReader.parse(this, inputs);

        // tokenizer sources -> token streams
        return MmlTokenizer.tokenize(tokenizerSources);
    }

    // used by language server and compiler.
    fun buildSemanticTree(tokens: MmlTokenSet): MmlSemanticTreeSet {
        // token streams -> semantic trees
        return MmlSemanticTreeBuilder.compile(tokens, this);
    }

    fun generateMusic(tree: MmlSemanticTreeSet): MidiMusic {
        // semantic trees -> simplified streams
        MmlMacroExpander.expand(tree, this);

        // simplified streams -> raw events
        var resolved = MmlEventStreamGenerator.generate(tree, this);

        // raw events -> SMF
        var smf = MmlSmfGenerator.generate(resolved);

        return smf;
    }
}

class MmlPrimitiveOperation {
    companion object {

        lateinit var all: List<MmlPrimitiveOperation>

        val Print = MmlPrimitiveOperation().apply { name = "__PRINT" }
        val Let = MmlPrimitiveOperation().apply { name = "__LET" };
        val Store = MmlPrimitiveOperation().apply { name = "__STORE" };
        val StoreFormat = MmlPrimitiveOperation().apply { name = "__STORE_FORMAT" };
        val Format = MmlPrimitiveOperation().apply { name = "__FORMAT" };
        val Apply = MmlPrimitiveOperation().apply { name = "__APPLY" };
        val Midi = MmlPrimitiveOperation().apply { name = "__MIDI" };
        val SyncNoteOffWithNext =
            MmlPrimitiveOperation().apply { name = "__SYNC_NOFF_WITH_NEXT" };
        val OnMidiNoteOff = MmlPrimitiveOperation().apply { name = "__ON_MIDI_NOTE_OFF" };
        val MidiMeta = MmlPrimitiveOperation().apply { name = "__MIDI_META" };
        val SaveOperationBegin = MmlPrimitiveOperation().apply { name = "__SAVE_OPER_BEGIN" };
        val SaveOperationEnd = MmlPrimitiveOperation().apply { name = "__SAVE_OPER_END" };
        val RestoreOperation = MmlPrimitiveOperation().apply { name = "__RESTORE_OPER" };
        val LoopBegin = MmlPrimitiveOperation().apply { name = "__LOOP_BEGIN" };
        val LoopBreak = MmlPrimitiveOperation().apply { name = "__LOOP_BREAK" };
        val LoopEnd = MmlPrimitiveOperation().apply { name = "__LOOP_END" };
        //#if !UNHACK_LOOP
        //val LoopBegin2 =  MmlPrimitiveOperation().apply { name = "[" };
        //val LoopBreak2 =  MmlPrimitiveOperation().apply { name = ":" };
        //val LoopBreak3 =  MmlPrimitiveOperation().apply { name = "/" };
        //val LoopEnd2 =  MmlPrimitiveOperation().apply { name = "]" };
        //#endif

        init {
            all = listOf(
                Print,
                Let,
                Store,
                StoreFormat,
                Format,
                Apply,
                Midi,
                SyncNoteOffWithNext,
                OnMidiNoteOff,
                MidiMeta,
                SaveOperationBegin,
                SaveOperationEnd,
                RestoreOperation,
                LoopBegin,
                LoopBreak,
                LoopEnd,
                //#if !UNHACK_LOOP
                //LoopBegin2, LoopBreak2, LoopBreak3, LoopEnd2
                //#endif
            )
        }
    }

    lateinit var name: String
}

enum class MmlDataType {
    Any,
    Number,
    Length,
    String,
    Buffer,
}

/*struct*/ class MmlLength {
    constructor(num: Int) {
        dots = 0;
        isValueByStep = false;
        number = num;
    }

    val number: Int
    val dots: Int
    var isValueByStep: Boolean

    fun getSteps(numerator: Int): Int {
        if (isValueByStep)
            return number
        if (number == 0)
            return 0;
        var basis = numerator / number;
        var ret = basis;
        for (i in 0 until dots) {
            basis /= 2
            ret += basis;
        }
        return ret;
    }

    override fun toString() = "[${if (isValueByStep) "%" else ""}$number${".".repeat(dots)}]"
}

class MmlException(message: String = "ML error", innerException: Exception? = null) :
    Exception(message, innerException) {

    companion object {
        fun formatMessage(message: String, location: MmlLineInfo): String {
            if (location == null)
                return message;
            return "$message (${location.file} line ${location.lineNumber} column ${location.linePosition})"
        }
    }

    constructor (message: String, location: MmlLineInfo)
            : this(message, location, null) {
    }

    constructor (message: String, location: MmlLineInfo, innerException: Exception?)
            : this(formatMessage(message, location), innerException) {
    }
}

fun MidiMusic.save(
    output: OutputStream,
    disableRunningStatus: Boolean = false,
    metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)? = null
) {
    var writer = SmfWriter(output).apply { this.disableRunningStatus = disableRunningStatus };
    if (metaWriter != null)
        writer.metaEventWriter = metaWriter!!
    writer.writeMusic(this);
}

fun MidiMusic.toBytes(
    disableRunningStatus: Boolean = false,
    metaWriter: ((Boolean, MidiMessage, OutputStream?) -> Int)? = null
): Array<Byte> {
    var ms = ByteArrayOutputStream();
    this.save(ms, disableRunningStatus, metaWriter);
    return ms.toByteArray().toTypedArray()
}
