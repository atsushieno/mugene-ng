@file:OptIn(ExperimentalJsExport::class)

package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi1Music
import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.write
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
@JsName("midiMusicToByteArray")
fun midiMusicToByteArray(music: Midi1Music): ByteArray {
    val list = mutableListOf<Byte>()
    music.write(list)
    return list.toByteArray()
}

@JsExport
@JsName("midi2MusicToByteArray")
fun midi2MusicToByteArray(music: Midi2Music): ByteArray {
    val list = mutableListOf<Byte>()
    music.write(list)
    return list.toByteArray()
}
