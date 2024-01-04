package dev.atsushieno.mugene

import kotlinx.cinterop.*
import platform.posix.readlink
import platform.posix.realpath

@OptIn(ExperimentalForeignApi::class)
internal actual fun getRealpath(file: String) : String {
    memScoped {
        val buffer = allocArray<ByteVar>(4096)
        realpath(file, buffer)
        return buffer.toKString()
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun getReadLinkPath(file: String) : String {
    memScoped {
        val buffer = allocArray<ByteVar>(4096)
        readlink(file, buffer, 4096u)
        return buffer.toKString()
    }
}

actual fun getSelfExecutablePath() = getReadLinkPath("/proc/self/exe")
