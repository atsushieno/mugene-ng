package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.Midi2Track
import dev.atsushieno.ktmidi.MidiEvent
import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.MidiTrack
import dev.atsushieno.ktmidi.Ump
import dev.atsushieno.ktmidi.umpfactory.*
import dev.atsushieno.ktmidi.toUnsigned
import kotlin.experimental.and

class MmlSmfGenerator(private val source: MmlResolvedMusic) {
    companion object {
        fun generate(source: MmlResolvedMusic): MidiMusic {
            val gen = MmlSmfGenerator(source)
            gen.generateSong()
            return gen.result!!
        }
    }

    var result: MidiMusic = MidiMusic().apply { deltaTimeSpec = source.baseCount / 4 }

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

class MmlMidi2Generator(private val source: MmlResolvedMusic) {
    companion object {
        fun generate(source: MmlResolvedMusic): Midi2Music {
            val gen = MmlMidi2Generator(source)
            gen.generateSong()
            return gen.result
        }
    }

    var result: Midi2Music = Midi2Music()

    private fun generateSong() {
        for (t in source.tracks)
            result.tracks.add(generateTrack(t))
    }

    private fun generateTrack(source: MmlResolvedTrack): Midi2Track {
        val rtrk = Midi2Track()
        var cur = 0
        for (ev in source.events) {
            var wasSysex = false
            lateinit var evt: Ump
            if (ev.arguments[0] == 0xFF.toByte())
                println("META events are not implemented in MIDI2 generator") // FIXME: we have to determine how to deal with META events
            else if (ev.arguments[0] == 0xFF.toByte())
                wasSysex = true // later
            else if ((ev.arguments[0].toUnsigned() and 0xF0) == 0xF0)
                evt = Ump(umpSystemMessage(0, ev.arguments[0], ev.arguments[1], ev.arguments[2]))
            else
                evt = Ump(umpMidi1Message(0,
                    (ev.arguments[0] and 0xF0.toByte()),
                    ev.arguments[0] % 0x10,
                    ev.arguments[1],
                    ev.arguments[2]))
            if (ev.tick > 0)
                rtrk.messages.addAll(umpJRTimestamps(0, ev.tick.toLong() - cur).map { i -> Ump(i) })

            if (wasSysex)
                umpSysex7Process(0, ev.arguments.drop(1),
                    { lv, _ -> rtrk.messages.add(Ump((lv / 0x100000000).toInt(), (lv % 0x100000000).toInt())) }, null)
            else
                rtrk.messages.add(evt)

            cur = ev.tick
        }

        // end of sequence
        rtrk.messages.add(Ump(umpSystemMessage(0,0xFF.toByte(), 0x2F, 0)))

        return rtrk
    }
}

