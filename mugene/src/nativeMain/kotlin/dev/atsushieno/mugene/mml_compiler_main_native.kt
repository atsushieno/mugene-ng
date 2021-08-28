package dev.atsushieno.mugene

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite

class MmlCompilerNative : MmlCompilerConsole() {
    override var resolver : StreamResolver = MergeStreamResolver(NativeDevResourceStreamResolver(), LocalFileStreamResolver(), ExecutablePathStreamResolver())

    override fun decodeStringUsingEncoding(s: String, charset: String): ByteArray =
        TODO("Not Implemented")

    override fun writeToFile(filename: String, bytes: ByteArray) {
        val fp = fopen(filename, "w+") ?: throw IllegalArgumentException("File '$filename' is not writable")
        bytes.usePinned { bytesPinned ->
            fwrite(bytesPinned.addressOf(0), bytes.size.convert(), 1.convert(), fp)
        }
        fclose(fp)
    }
}

internal actual fun createDefaultCompiler() : MmlCompiler = MmlCompilerNative()
