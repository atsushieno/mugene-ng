package dev.atsushieno.mugene

import java.io.FileOutputStream
import java.nio.charset.Charset

fun main(args: Array<String>) {
    try {
        MmlCompilerConsole.create().compile(args.toList())
    } catch (ex: MmlException) {
        println(ex.message)
    }
}

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
