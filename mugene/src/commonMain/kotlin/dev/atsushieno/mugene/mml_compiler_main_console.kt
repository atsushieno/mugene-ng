package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.SmfWriterExtension

abstract class MmlCompilerConsole : MmlCompiler() {
    companion object {
        fun create(): MmlCompilerConsole = createDefaultCompiler() as MmlCompilerConsole
    }

    class MmlCompilerOptions {
        var skipDefaultMmlFiles = false
        var disableRunningStatus = false
        var metaWriter: ((Boolean, MidiMessage, MutableList<Byte>) -> Int)? = null
    }

    abstract fun writeToFile(filename: String, bytes: ByteArray)

    fun compile(args: List<String>) {
        try {
            compileCore(args)
        } catch (ex: MmlException) {
            println(ex.message)
        }
    }

    private val help = """MML compiler mugene

Usage: mugene [options] mml_files

Options:
    --midi2
        outputs MIDI 2.0 UMP based music format file (with JR Timestamp).
    --midi2x
        outputs MIDI 2.0 UMP based music format file with SMF-style delta time.
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
        This option is for core MML operation hackers.
"""

    fun compileCore(args: List<String>) {

        if (!args.any()) {
            report(MmlDiagnosticVerbosity.Error, null, help)
            return
        }

        // file names -> input sources
        val inputFilenames = mutableListOf<String>()
        var outFilename = ""
        var explicitFilename: String? = null
        var disableRunningStatus = false
        var extension = ".mid"
        val metaWriter = SmfWriterExtension.DEFAULT_META_EVENT_WRITER
        var noDefault = false
        var midi2 = false
        var outputDeltaTime = false

        for (arg in args) {
            when (arg) {
                "--midi2" -> {
                    midi2 = true
                    extension = ".umps"
                    continue
                }
                "--midi2x" -> {
                    midi2 = true
                    outputDeltaTime = true
                    extension = ".umpx"
                    continue
                }
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
                        MmlValueExprResolver.stringToBytes = { s -> decodeStringUsingEncoding(s, enc) }
                        continue
                    }
                    if (arg.startsWith("--output:")) {
                        explicitFilename = arg.substring(9)
                        continue
                    }
                    if (arg == "--help") {
                        report(MmlDiagnosticVerbosity.Error, null, help)
                        return
                    }
                }
            }
            val lastIndex = arg.lastIndexOf('.')
            val fileWithoutExtension = if (lastIndex < 0) arg else arg.substring(0, lastIndex)
            outFilename = fileWithoutExtension + extension
            inputFilenames.add(arg)
        }
        if (explicitFilename != null)
            outFilename = explicitFilename

        // FIXME: stream resolver should be processed within the actual parsing phase.
        // This makes it redundant to support #include
        val inputs = mutableListOf<MmlInputSource>()
        for (fname in inputFilenames)
            inputs.add(MmlInputSource(fname, resolver.getEntity(fname)))

        val outputBytes = mutableListOf<Byte>()
        if (midi2)
            compile2(outputDeltaTime, noDefault, inputs, outputBytes)
        else
            compile(noDefault, inputs, metaWriter, outputBytes, disableRunningStatus)

        writeToFile(outFilename, outputBytes.toByteArray())
        report(
            MmlDiagnosticVerbosity.Information,
            null,
            "Written SMF file ... $outFilename")
    }
}