package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriter
import dev.atsushieno.ktmidi.write
import kotlin.js.JsExport
import kotlin.js.JsName

@JsExport
class JsInteropSupport {
    companion object {
        @JsName("midiMusicToByteArray")
        fun midiMusicToByteArray(music: MidiMusic): ByteArray {
            val list = mutableListOf<Byte>()
            SmfWriter(list).writeMusic(music)
            return list.toByteArray()
        }

        @JsName("midi2MusicToByteArray")
        fun midi2MusicToByteArray(music: Midi2Music): ByteArray {
            val list = mutableListOf<Byte>()
            music.write(list)
            return list.toByteArray()
        }
    }
}
