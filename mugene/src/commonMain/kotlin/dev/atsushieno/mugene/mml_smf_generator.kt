package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.MidiEvent
import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.MidiTrack

class MmlSmfGenerator(private val source: MmlResolvedMusic) {
    companion object {
        fun generate(source: MmlResolvedMusic): MidiMusic {
            val gen = MmlSmfGenerator(source)
            gen.generateSong()
            return gen.result!!
        }
    }

    var result: MidiMusic = MidiMusic().apply { deltaTimeSpec = (source.baseCount / 4).toByte() }

    private fun generateSong() {
        for (t in source.tracks)
            result.tracks.add(generateTrack(t))
    }

    private fun generateTrack(source: MmlResolvedTrack): MidiTrack {
        val rtrk = MidiTrack()
        var cur = 0
        for (ev in source.events) {
            var evt: MidiEvent? = null
            if (ev.arguments[0] == 0xFF.toByte())
                evt = MidiEvent(
                    ev.arguments[0].toUByte().toInt(),
                    ev.arguments[1].toUByte().toInt(),
                    0,
                    ev.arguments.drop(2).toByteArray()
                )
            else if (ev.arguments.size == 3)
                evt = MidiEvent(
                    ev.arguments[0].toUByte().toInt(),
                    ev.arguments[1].toUByte().toInt(),
                    ev.arguments[2].toUByte().toInt(),
                    null
                )
            else
                evt = MidiEvent(ev.arguments[0].toUByte().toInt(), 0, 0, ev.arguments.drop(1).toByteArray())
            val msg = MidiMessage(ev.tick - cur, evt)
            rtrk.messages.add(msg)
            cur = ev.tick
        }
        rtrk.messages.add(MidiMessage(0, MidiEvent(0xFF, 0x2F, 0, byteArrayOf())))
        return rtrk
    }

}