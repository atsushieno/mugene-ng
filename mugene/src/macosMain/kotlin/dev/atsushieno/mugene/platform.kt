package dev.atsushieno.mugene

import kotlinx.cinterop.*
import platform.darwin.SizeVar
import platform.osx.proc_pidpath
import platform.posix.getpid
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
internal actual fun getSelfExecutablePath() : String {
    memScoped {
        val buffer = allocArray<ByteVar>(4096)
        val pid = getpid()
        if (proc_pidpath(pid, buffer, 4096u) < 0)
            throw Exception()
        return buffer.toKString()
    }
}
