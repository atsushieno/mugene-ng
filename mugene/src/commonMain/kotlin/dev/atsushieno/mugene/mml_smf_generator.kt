package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.Midi2Track
import dev.atsushieno.ktmidi.MidiEvent
import dev.atsushieno.ktmidi.MidiMessage
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.MidiTrack
import dev.atsushieno.ktmidi.Ump
import dev.atsushieno.ktmidi.UmpFactory
import kotlin.experimental.and

internal fun Byte.toUnsigned() : Int = if (this < 0) this.toUByte().toInt() else this.toInt()

class MmlSmfGenerator(private val source: MmlResolvedMusic) {
    companion object {
        fun generate(source: MmlResolvedMusic): MidiMusic {
            val gen = MmlSmfGenerator(source)
            gen.generateSong()
            return gen.result
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
            var evt: MidiEvent?
            if (ev.arguments[0] == 0xFF.toByte())
                evt = MidiEvent(
                    ev.arguments[0].toUnsigned(),
                    ev.arguments[1].toUnsigned(),
                    0,
                    ev.arguments.drop(2).toByteArray()
                )
            else if (ev.arguments.size == 3)
                evt = MidiEvent(
                    ev.arguments[0].toUnsigned(),
                    ev.arguments[1].toUnsigned(),
                    ev.arguments[2].toUnsigned(),
                    null
                )
            else
                evt = MidiEvent(ev.arguments[0].toUnsigned(), 0, 0, ev.arguments.drop(1).toByteArray())
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
                evt = Ump(UmpFactory.systemMessage(0, ev.arguments[0], ev.arguments[1], ev.arguments[2]))
            else if (ev.operation == "MIDI_NG") {
                val rest32 = ev.arguments[4].toUnsigned() * 0x1000000 + ev.arguments[5].toUnsigned() * 0x10000 +
                        ev.arguments[6].toUnsigned() * 0x100 + ev.arguments[7].toUnsigned()
                val umpLong = UmpFactory.midi2ChannelMessage8_8_32(
                    ev.arguments[1].toUnsigned() / 0x10,
                    ev.arguments[0].toUnsigned(),
                    ev.arguments[1].toUnsigned() % 0x10,
                    ev.arguments[2].toUnsigned(),
                    ev.arguments[3].toUnsigned(),
                    rest32.toUInt().toLong()
                )
                evt = Ump(umpLong)
            }
            else
                evt = Ump(UmpFactory.midi1Message(0,
                    (ev.arguments[0] and 0xF0.toByte()),
                    ev.arguments[0] % 0x10,
                    ev.arguments[1],
                    ev.arguments[2]))
            if (ev.tick != cur)
                rtrk.messages.addAll(UmpFactory.jrTimestamps(0, ev.tick.toLong() - cur).map { i -> Ump(i) })

            if (wasMetaSysex8)
                // those extra 4 bytes are for sysex ManufacturerID, deviceID, subID1, and subID2. They are all dummy values. Then 3 0xFF bytes.
                rtrk.messages.addAll(UmpFactory.sysex8(0, listOf(0, 0, 0, 0, 0xFF, 0xFF, 0xFF).map { it.toByte() } + ev.arguments.drop(1)))
            else if (wasSysex)
                rtrk.messages.addAll(UmpFactory.sysex7(0, ev.arguments.drop(1)))
            else
                rtrk.messages.add(evt)

            cur = ev.tick
        }

        // end of sequence
        rtrk.messages.add(Ump(UmpFactory.systemMessage(0,0xFF.toByte(), 0x2F, 0)))

        return rtrk
    }
}

