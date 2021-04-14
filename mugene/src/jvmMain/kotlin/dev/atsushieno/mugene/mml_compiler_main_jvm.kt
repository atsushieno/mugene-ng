package dev.atsushieno.mugene

import java.io.FileOutputStream
import java.nio.charset.Charset

class MmlCompilerJvm : MmlCompilerConsole() {
    override var resolver : StreamResolver = MergeStreamResolver(LocalFileStreamResolver(), JarResourceStreamResolver())

    override fun decodeStringUsingEncoding(s: String, charset: String): ByteArray =
        Charset.forName(charset).encode(s).array()

    override fun writeToFile(filename: String, bytes: ByteArray) =
        FileOutputStream(filename).use {
            it.write(bytes)
        }
}

internal actual fun createDefaultCompiler() : MmlCompiler = MmlCompilerJvm()
