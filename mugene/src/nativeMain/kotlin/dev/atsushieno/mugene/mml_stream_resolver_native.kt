package dev.atsushieno.mugene

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.posix.realpath
import platform.posix.stat

//region input sources to tokenizer sources

open class LocalFileStreamResolver : StreamResolver() {

    private fun exists(file: String) : Boolean = stat(file, null) == 0
    private fun getRealpath(file: String) : String {
        memScoped {
            val buffer = allocArray<ByteVar>(4096)
            realpath(file, buffer)
            return buffer.toKString()
        }
    }
    override fun resolveFilePath(file: String): String? {
        if (!includes.any()) {
            if (!exists(file))
                return file
            return getRealpath(file)
        }
        if (exists(file) && getRealpath(file) == file)
            return file
        return getRealpath(includes.last() + '/' + file)
    }

    override fun onGetEntity(file: String): String? {
        val filePath = resolveFilePath(file)

        val returnBuffer = StringBuilder()
        val fp = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open '$filePath'")

        try {
            memScoped {
                val bufferSize = 64 * 1024
                val buffer = allocArray<ByteVar>(bufferSize)
                var line = fgets(buffer, bufferSize, fp)?.toKString()
                while (line != null) {
                    returnBuffer.append(line)
                    line = fgets(buffer, bufferSize, fp)?.toKString()
                }
            }
        } finally {
            fclose(fp)
        }

        return returnBuffer.toString()
    }
}

// I assume this only works for dev. environment for unit testing. We need more paths to resolve...
class NativeDevResourceStreamResolver : LocalFileStreamResolver() {

    override fun resolveFilePath(file: String): String? {
        return super.resolveFilePath("build/processedResources/native/main/$file")
    }
}
