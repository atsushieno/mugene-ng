package dev.atsushieno.mugene

actual fun getSelfExecutablePath() : String {
    val buffer = allocArray<ByteVar>(4096)
    GetModuleFileName(null, buffer, 4096)
    return buffet.toKString()
}

