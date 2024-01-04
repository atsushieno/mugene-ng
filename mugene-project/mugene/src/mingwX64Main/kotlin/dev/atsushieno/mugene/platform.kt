package dev.atsushieno.mugene

import kotlinx.cinterop.*
import platform.windows.GetFullPathNameW
import platform.windows.GetModuleFileName
import platform.windows.WCHARVar

@OptIn(ExperimentalForeignApi::class)
internal actual fun getRealpath(file: String) : String {
    memScoped {
        val buffer = allocArray<WCHARVar>(4096)
        GetFullPathNameW(file, 4096u, buffer, null)
        return buffer.toKString()
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun getSelfExecutablePath() : String {
    memScoped {
        val buffer = allocArray<WCHARVar>(4096)
        GetModuleFileName!!(null, buffer, 4096u)
        return buffer.toKString()
    }
}

