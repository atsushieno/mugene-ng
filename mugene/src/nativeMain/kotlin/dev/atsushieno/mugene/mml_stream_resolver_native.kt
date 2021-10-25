package dev.atsushieno.mugene

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.CVariable
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.nativeNullPtr
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.posix.NULL
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.posix.readlink
import platform.posix.realpath
import platform.posix.stat

//region input sources to tokenizer sources

internal fun getRealpath(file: String) : String {
    memScoped {
        val buffer = allocArray<ByteVar>(4096)
        realpath(file, buffer)
        return buffer.toKString()
    }
}

open class LocalFileStreamResolver : StreamResolver() {

    internal fun exists(file: String) : Boolean {
        val st = cValue<stat>()
        return stat(file, st) == 0
    }

    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        val abs = if (baseSourcePath != null && exists(baseSourcePath)) {
            val bAbs = getRealpath(baseSourcePath)
            // FIXME: any solid solution for resolving relative paths?
            bAbs.substring(0, bAbs.lastIndexOf('/') + 1) + file
        } else file
        if (!includes.any()) {
            if (!exists(abs))
                return null
            val realPath = getRealpath(abs)
            return if (exists(realPath)) realPath else null
        }
        if (exists(abs) && getRealpath(abs) == abs)
            return abs
        return getRealpath(includes.last() + '/' + abs)
    }

    override fun onGetEntity(resolvedPath: String): String? {
        val returnBuffer = StringBuilder()
        val fp = fopen(resolvedPath, "r") ?: throw IllegalArgumentException("Cannot open '$resolvedPath'")

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
    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        return super.resolveFilePath(file, baseSourcePath) ?: super.resolveFilePath(file, "build/processedResources/native/main/")
    }
}

class ExecutablePathStreamResolver : LocalFileStreamResolver() {

    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        val exePath = getSelfExecutablePath()
        val dir = exePath.substring(0, exePath.lastIndexOfAny(charArrayOf('/', '\\')))
        val filePath = super.resolveFilePath(file, dir)
        return if (filePath != null && exists(filePath)) filePath else null
    }
}

expect fun getSelfExecutablePath() : String
