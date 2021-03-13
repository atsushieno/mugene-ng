package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriter
import dev.atsushieno.ktmidi.SmfWriterExtension
import java.io.*
import java.nio.charset.Charset



class MmlCompilerJvm : MmlCompiler() {
    override var resolver : StreamResolver = MergeStreamResolver(LocalFileStreamResolver(), JarResourceStreamResolver())

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
            report(MmlDiagnosticVerbosity.Error, null, help)
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
                        report(MmlDiagnosticVerbosity.Error, null, help)
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
            "Written SMF file ... $outFilename")
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
