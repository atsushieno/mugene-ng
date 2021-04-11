package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.Midi2Track
import dev.atsushieno.ktmidi.MidiEvent
import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.MidiTrack
import dev.atsushieno.ktmidi.Ump
import dev.atsushieno.ktmidi.umpfactory.*
import kotlin.experimental.and

internal fun Byte.toUnsigned() : Int = if (this < 0) 0x100 + this else this.toInt()

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

    var result: Midi2Music = Midi2Music().apply { deltaTimeSpec = source.baseCount / 4 }

    private fun generateSong() {
        for (t in source.tracks)
            result.tracks.add(generateTrack(t))
    }

    private fun generateTrack(source: MmlResolvedTrack): Midi2Track {
        val rtrk = Midi2Track()
        var cur = 0
        for (ev in source.events) {
            var wasSysex = false
            var wasMetaSysex8 = false
            lateinit var evt: Ump
            if (ev.arguments[0] == 0xFF.toByte())
                wasMetaSysex8 = true
            else if (ev.arguments[0] == 0xF0.toByte())
                wasSysex = true // later
            else if ((ev.arguments[0].toUnsigned() and 0xF0) == 0xF0)
                evt = Ump(umpSystemMessage(0, ev.arguments[0], ev.arguments[1], ev.arguments[2]))
            else if (ev.operation == "MIDI_NG") {
                val umpLong = umpMidi2ChannelMessage8_8_32(
                    ev.arguments[1] / 0x10,
                    ev.arguments[0],
                    ev.arguments[1] % 0x10,
                    ev.arguments[2].toInt(),
                    ev.arguments[3].toInt(),
                    ev.arguments[4] * 0x1000000 + ev.arguments[5] * 0x10000 + ev.arguments[6] * 0x100 + ev.arguments[7].toLong()
                )
                evt = Ump((umpLong shr 32).toInt(), (umpLong and 0xFFFFFFFF).toInt())
            }
            else
                evt = Ump(umpMidi1Message(0,
                    (ev.arguments[0] and 0xF0.toByte()),
                    ev.arguments[0] % 0x10,
                    ev.arguments[1],
                    ev.arguments[2]))
            if (ev.tick != cur)
                rtrk.messages.addAll(umpJRTimestamps(0, ev.tick.toLong() - cur).map { i -> Ump(i) })

            if (wasMetaSysex8)
                // those extra 4 bytes are for sysex ManufacturerID, deviceID, subID1, and subID2. They are all dummy values.
                umpSysex8Process(0, listOf<Byte>(0, 0, 0, 0) + ev.arguments.drop(1), ev.arguments.size + 4 - 1, 0,
                    { lv1, lv2, _ -> rtrk.messages.add(Ump((lv1 / 0x100000000).toInt(), (lv1 % 0x100000000).toInt(), (lv2 / 0x100000000).toInt(), (lv2 % 0x100000000).toInt())) }, null)
            else if (wasSysex)
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

