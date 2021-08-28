package dev.atsushieno.mugene

actual fun getSelfExecutablePath() : String {
    val buffer = allocArray<ByteVar>(4096)
    val size = cValue();
    if (_NSGetExecutablePath(buffer, size) == 0)
        return buffer.toKString()
    else throw kotlin.Exception()
}
