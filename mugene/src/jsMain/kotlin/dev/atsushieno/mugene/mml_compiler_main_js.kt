package dev.atsushieno.mugene

private external fun require(module: String): dynamic
private val fs = if(js("typeof(process) !== 'undefined'") as Boolean) require("fs") else null
private val Buffer = require("buffer")

class MmlCompilerJs : MmlCompilerConsole() {
    override var resolver : StreamResolver = MergeStreamResolver(JsDevResourceStreamResolver(), LocalFileStreamResolver())

    override fun decodeStringUsingEncoding(s: String, charset: String): ByteArray =
        TODO("Not implemented")

    override fun writeToFile(filename: String, bytes: ByteArray) =
        fs.writeFile(filename, Buffer.from(bytes)) { err -> println(err) }
}

internal actual fun createDefaultCompiler() : MmlCompiler = MmlCompilerJs()
