package dev.atsushieno.mugene

internal actual fun getSelfExecutablePath(): String = throw UnsupportedOperationException("iOS does not support getSelfExecutablePath()")
internal actual fun getRealpath(file: String) : String = throw UnsupportedOperationException("iOS does not support getRealpath()")
