package dev.atsushieno.mugene

// FIXME: implement them

class MmlCompilerJs : MmlCompilerConsole() {
    override var resolver : StreamResolver = MergeStreamResolver()

    override fun decodeStringUsingEncoding(s: String, charset: String): ByteArray =
        TODO("Not implemented")

    override fun writeToFile(filename: String, bytes: ByteArray) =
        TODO("Not implemented")
}

internal actual fun createDefaultCompiler() : MmlCompiler = MmlCompilerJs()
