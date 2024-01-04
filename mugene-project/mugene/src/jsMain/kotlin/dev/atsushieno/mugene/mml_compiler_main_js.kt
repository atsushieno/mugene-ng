@file:OptIn(ExperimentalJsExport::class)

package dev.atsushieno.mugene

import com.strumenta.antlrkotlin.runtime.assert
import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.Midi1Music

private external fun require(module: String): dynamic
private val fs = if(js("typeof(process) !== 'undefined'") as Boolean) require("fs") else null
private val Buffer = require("buffer")

class MmlCompilerJs : MmlCompilerConsole() {
    override var resolver : StreamResolver = MergeStreamResolver(NodeModuleResourceStreamResolver.instance, JsDevResourceStreamResolver(), LocalFileStreamResolver())

    override fun decodeStringUsingEncoding(s: String, charset: String): ByteArray =
        TODO("Not implemented")

    override fun writeToFile(filename: String, bytes: ByteArray) =
        fs.writeFile(filename, Buffer.from(bytes)) { err -> println(err) }
}

internal actual fun createDefaultCompiler() : MmlCompiler {
    return MmlCompilerJs().also {
        // FIXME: these are added here only to retain those methods.
        //  There should be some way to leave those functions in the generated .js code.
        midiMusicToByteArray(Midi1Music())
        midi2MusicToByteArray(Midi2Music())
        assert(NodeModuleResourceStreamResolver.instance.basePath.isNotEmpty())
    }
}

@JsExport
fun createJSCompilerForExport() = createDefaultCompiler()
