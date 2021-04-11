package dev.atsushieno.mugene

import Buffer

class MmlCompilerJs : MmlCompilerConsole() {
    override var resolver : StreamResolver = MergeStreamResolver(LocalFileStreamResolver())

    override fun decodeStringUsingEncoding(s: String, charset: String): ByteArray =
        TODO("Not implemented")

    override fun writeToFile(filename: String, bytes: ByteArray) =
        fs.writeFile(filename, Buffer.from(bytes)) { err -> println(err) }
}

