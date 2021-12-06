package dev.atsushieno.mugene

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.readlink

internal fun getReadLinkPath(file: String) : String {
    memScoped {
        val buffer = allocArray<ByteVar>(4096)
        readlink(file, buffer, 4096)
        return buffer.toKString()
    }
}

actual fun getSelfExecutablePath() = getReadLinkPath("/proc/self/exe")
