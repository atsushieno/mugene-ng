package dev.atsushieno.mugene

class MmlCompilerNative : MmlCompilerConsole() {
    override var resolver : StreamResolver = MergeStreamResolver(LocalFileStreamResolver())

    override fun decodeStringUsingEncoding(s: String, charset: String): ByteArray =
        TODO("Not Implemented")

    override fun writeToFile(filename: String, bytes: ByteArray) =
        TODO("Not Implemented")
}

internal actual fun createDefaultCompiler() : MmlCompiler = MmlCompilerNative()
