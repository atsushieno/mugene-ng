
package dev.atsushieno.mugene

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.MidiChannelStatus
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.SmfWriter
import dev.atsushieno.ktmidi.eventType
import dev.atsushieno.ktmidi.read
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MmlCompilerTest {
    @Test
    fun negativeStepConstantArgument() {
        MmlTestUtility.testCompile("note-macro", "#macro x arg:length=%-1 { }", true)
    }

    @Test
    fun argumentWithoutCurly() {
        MmlTestUtility.testCompile("note-macro", "#macro DEBUG val { __PRINT \$val }", true)
    }

    @Test
    fun argumentOptCurly() {
        MmlTestUtility.testCompile("note-macro", "#macro DEBUG val { __PRINT {\$val} }", true)
    }

    @Test
    fun macroArgumentsProcessed() {
        MmlTestUtility.testCompile(
            "arguments",
            "#variable __octave:number = 5\n#macro o val:number { __LET{\"__octave\", \$val} }\n1   o5",
            true
        )
    }

    @Test
    fun negativeVariableBinding() {
        MmlTestUtility.testCompile(
            "arguments",
            "#variable __trans_c:number = 0\n#macro Kc- { __LET{\"__trans_c\", -1} }",
            true
        )
    }

    @Test
    fun compileNoteMacro() {
        MmlTestUtility.testCompile(
            "note-macro", """
#macro n key:number, step:length=${'$'}__length, gate:length=%-1, vel:number=${'$'}__velocity, timing:number=${'$'}__timing, offvel:number=0   { \
	__LET{"__current_note_step", ${'$'}gate + %1 ? ${'$'}gate, ${'$'}step }  \
	__LET{"__current_note_gate", ${'$'}__current_note_step * ${'$'}__gate_rel * {1 / ${'$'}__gate_rel_denom} - ${'$'}__gate_abs} \
	__LET{"__current_note_gate", ${'$'}__current_note_gate \< 0 ? 0, ${'$'}__current_note_gate } \
	NOP${'$'}timing \
	NON${'$'}key, ${'$'}vel \
	NOP${'$'}__current_note_gate \
	__ON_MIDI_NOTE_OFF{${'$'}__current_note_gate, ${'$'}key, ${'$'}vel} \
	NOFF${'$'}key, ${'$'}offvel \
	NOP${'$'}step - ${'$'}__current_note_gate \
	NOP0-${'$'}timing }
        """, true
        )
    }

    @Test
    fun simpleTrackAndNotes() {
        val actual = MmlTestUtility.testCompile("SimpleCompilation", "1   o5cde")
        val expected = intArrayOf(
            U.M, U.T, L.h, L.d, 0, 0, 0, 6, 0, 1, 0, 1, 0, 0x30, // at 14
            U.M, U.T, L.r, L.k, 0, 0, 0, 0x1C, // at 22
            0, 0x90, 0x3C, 100,
            0x30, 0x80, 0x3C, 0,
            0, 0x90, 0x3E, 100,
            0x30, 0x80, 0x3E, 0,
            0, 0x90, 0x40, 100,
            0x30, 0x80, 0x40, 0,
            0, 0xFF, 0x2F, 0).map { i -> i.toByte() }.toByteArray()
        assertArrayEquals(expected.toTypedArray(), actual.toTypedArray(), "MIDI bytes")
        val music = MidiMusic()
        music.read(actual.toList())
        assertEquals(144, music.getTotalTicks(), "total ticks")
    }

    @Test
    fun simpleTrackAndNotesMidi2() {
        val actual = MmlTestUtility.testCompile2("SimpleCompilation", "1   o5cde")
        for(b in actual) print(b.toUnsigned().toString(16) + ':')
    }

    @Test
    fun simpleLoopRest() {
        MmlTestUtility.testCompile("SimpleCompilation", "#macro r len:number {  }\n1   [r1]4", true)
    }

    @Test
    fun simpleMacroDefinition() {
        MmlTestUtility.testCompile("Macro definition", "#macro\tCH_INIT ch:number { CH\$ch }")
    }
    @Test
    fun macroWithMultipleOperationUses() {
        MmlTestUtility.testCompile("Macro definition", "#macro\tCH_INIT ch:number { CH\$ch E127 }")
        MmlTestUtility.testCompile(
            "Macro definition",
            "#macro\tCH_INIT ch:number { CH\$ch E127 B0 P64 M0 H0 RSD0 CSD0 DSD0 v80 l8 q16 }"
        )
    }

    @Test
    fun metaTitle() {
        val actual = MmlTestUtility.testCompile("meta", "#meta title \"test\"")
        val expected = intArrayOf(
            U.M, U.T, L.h, L.d, 0, 0, 0, 6, 0, 1, 0, 1, 0, 0x30,
            U.M, U.T, L.r, L.k, 0, 0, 0, 0x0C,
            0, 0xFF, 3, 4, L.t, L.e, L.s, L.t,
            0, 0xFF, 0x2F, 0).map { i -> i.toByte() }.toByteArray()
        assertArrayEquals(expected.toTypedArray(), actual.toTypedArray(), "MIDI bytes")
    }

    // U and L cannot share case-insensitively identical fields for JNI signature...
    class U {
        companion object {
            val M = 'M'.toInt()
            val T = 'T'.toInt()
        }
    }

    class L {
        companion object {
            val h = 'h'.toInt()
            val d = 'd'.toInt()
            val r = 'r'.toInt()
            val k = 'k'.toInt()
            val e = 'e'.toInt()
            val s = 's'.toInt()
            val t = 't'.toInt()
        }
    }

    @Test
    fun lengthMustBeNonNegative() {
        val mml = """
1	CH4 @47 V100 P79 l16 v100
    E_64,127,0,4.,24
    [g24]12
    [c8c,,60c8c,,60c8c8 ]1
    """
        MmlTestUtility.testCompile2("midi2", mml)
        MmlTestUtility.testCompile("midi1", mml)
    }

    @Test
    fun noteSkippingLength() {
        val mml = """1	c8d8,,60e8"""
        val midi1Bytes = MmlTestUtility.testCompile("midi1", mml)
        val music = MidiMusic().apply { read(midi1Bytes.toList()) }
        assertEquals(72, music.getTotalTicks(), "midi1 total ticks")
        val midi2Bytes = MmlTestUtility.testCompile2("midi2", mml, outputDeltaTime = true)
        val music2 = Midi2Music().apply { read (midi2Bytes.toList()) }
        assertEquals(72, music2.getTotalTicks(), "midi2 total ticks")
    }

    @Test
    fun escapedNameCharExtension() {
        val mml = """
#macro M\1 { c }
#macro M\0 { c }
#macro M\$ { c }
#macro M\# { c }"""
        MmlTestUtility.testCompile("midi1", mml)
    }

    @Test
    fun compileStringInMidiPrimitive() {
        val mml = """
#macro X { __MIDI #F0, #7D, 11, "-1472549978", #F7 }
1   X
"""
        val music = MidiMusic().apply { read(MmlTestUtility.testCompile("midi1", mml).toList()) }
        assertEquals(1, music.tracks.size, "tracks.size")
        val msg = music.tracks[0].messages.first()
        assertEquals(0xF0, msg.event.statusByte.toUnsigned(), "status")
        val sysexData = arrayOf(0x7D, 0x0B, 0x2D, 0x31, 0x34, 0x37, 0x32, 0x35, 0x34, 0x39, 0x39, 0x37, 0x38)
            .map { v -> v.toByte() }.toTypedArray()
        val actualData = msg.event.extraData!!.drop(msg.event.extraDataOffset).take(msg.event.extraDataLength).toTypedArray()
        assertArrayEquals(sysexData, actualData, "sysex data")
        val bytes = mutableListOf<Byte>()
        SmfWriter(bytes).writeTrack(music.tracks[0])
        val trackHead = arrayOf('M'.code, 'T'.code, 'r'.code, 'k'.code, 0, 0, 0, 20, 0, 0xF0).map { v -> v.toByte() }.toTypedArray()
        val trackTail = arrayOf(0xF7.toByte(), 0, 0xFF.toByte(), 0x2F, 0)
        assertArrayEquals(trackHead + sysexData + trackTail, bytes.toTypedArray(), "SMF track")
    }

    @Test
    fun compileStringInMidiPrimitive2() {
        val mml = """
#macro X { __MIDI #F0, #7D, "augene-ng", 11, "-1472549978", #F7 }
1   X
"""
        val music = MidiMusic().apply { read(MmlTestUtility.testCompile("midi1", mml).toList()) }
        assertEquals(1, music.tracks.size, "tracks.size")
        val msg = music.tracks[0].messages.first()
        assertEquals(0xF0, msg.event.statusByte.toUnsigned(), "status")
        val sysexData = arrayOf(0x7D, 'a'.code, 'u'.code, 'g'.code, 'e'.code, 'n'.code, 'e'.code, '-'.code, 'n'.code, 'g'.code,
                                0x0B, 0x2D, 0x31, 0x34, 0x37, 0x32, 0x35, 0x34, 0x39, 0x39, 0x37, 0x38)
            .map { v -> v.toByte() }.toTypedArray()
        val actualData = msg.event.extraData!!.drop(msg.event.extraDataOffset).take(msg.event.extraDataLength).toTypedArray()
        assertArrayEquals(sysexData, actualData, "sysex data")
        val bytes = mutableListOf<Byte>()
        SmfWriter(bytes).writeTrack(music.tracks[0])
        val trackHead = arrayOf('M'.code, 'T'.code, 'r'.code, 'k'.code, 0, 0, 0, 29, 0, 0xF0).map { v -> v.toByte() }.toTypedArray()
        val trackTail = arrayOf(0xF7.toByte(), 0, 0xFF.toByte(), 0x2F, 0)
        assertArrayEquals(trackHead + sysexData + trackTail, bytes.toTypedArray(), "SMF track")
    }

    @Test
    fun verifyCompiledBinaries() {
        val mml = """
1   CH1 @1 o5 l4 v100 cdefgab>c
"""
        val music = MidiMusic().apply { read(MmlTestUtility.testCompile("midi1", mml).toList()) }
        assertEquals(1, music.tracks.size, "tracks.size")
        val messages = music.tracks[0].messages
        assertEquals(0xB0, messages[0].event.statusByte.toUnsigned(), "msg0")
        assertEquals(0xB0, messages[1].event.statusByte.toUnsigned(), "msg1")
        assertEquals(0xC0, messages[2].event.statusByte.toUnsigned(), "msg2")
        assertEquals(0x90, messages[3].event.statusByte.toUnsigned(), "msg3")
        assertEquals(0x80, messages[4].event.statusByte.toUnsigned(), "msg4")
        assertEquals(0x90, messages[5].event.statusByte.toUnsigned(), "msg5")
        assertEquals(0x80, messages[6].event.statusByte.toUnsigned(), "msg6")
    }

    @Test
    fun midi2PerNotePitchbend() {
        val mml = """
1   Bn=64,0 n64,4
"""
        val umpx = MmlTestUtility.testCompile2("midi2", mml).toList()
        val music = Midi2Music().apply { read(umpx) }
        assertEquals(0x40604000, music.tracks[0].messages[0].int1, "int1")
        assertEquals(0x80000000.toUInt(), music.tracks[0].messages[0].int2.toUInt(), "int2")
    }

    @Test
    fun midi2PerNotePitchbendCurrent() {
        val mml = """
1   BEND_CENT_MODE24 n64,0,4 Bc=0 r8 Bc=100 r8 Bc=-100
"""
        val umpx = MmlTestUtility.testCompile2("midi2", mml).toList()
        val music = Midi2Music().apply { read(umpx) }
        val ml = music.tracks[0].messages
        assertEquals(0x40604000, ml[1].int1, "1.int1")
        assertEquals(0x80000000.toUInt(), ml[1].int2.toUInt(), "1.int2")
        assertEquals(0x40604000, ml[3].int1, "3.int1")
        assertEquals(0x85555555.toUInt(), ml[3].int2.toUInt(), "3.int2")
        assertEquals(0x40604000, ml[6].int1, "6.int1")
        assertEquals(0x7AAAAAAA.toUInt(), ml[6].int2.toUInt(), "6.int2")
    }

    @Test
    fun midi2PerNotePitchbendCurrentSpectra() {
        val mml = """
1   BEND_CENT_MODE24 o5 c0,1 Bc_0,1200,8,2 f0,1 Bc_0,-1200,8,2 r1
"""
        val umpx = MmlTestUtility.testCompile2("midi2", mml).toList()
        val music = Midi2Music().apply { read(umpx) }
        assertTrue(music.tracks[0].messages.filter { it.int1 == 0x40603C00 }.size > 10, "PN.o5c")
        assertTrue(music.tracks[0].messages.filter { it.int1 == 0x40604100 }.size > 10, "PN.o5f")
    }

    @Test
    fun midi2PerNotePitchbendCurrentRel() {
        val mml = """
1   o5 c0,1 Bc=0 r4 Bc=-8192 r4 Bc+4096 r4 Bc-3072
"""
        val umpx = MmlTestUtility.testCompile2("midi2", mml).toList()
        val music = Midi2Music().apply { read(umpx) }
        val ml = music.tracks[0].messages
        assertEquals(0x40603C00, ml[1].int1, "1.int1")
        assertEquals(0x80000000.toInt(), ml[1].int2, "1.int2")
        assertEquals(0x40603C00, ml[3].int1, "3.int1")
        assertEquals(0x00000000, ml[3].int2, "3.int2")
        assertEquals(0x40603C00, ml[5].int1, "5.int1")
        assertEquals(0x40000000, ml[5].int2, "5.int2")
        assertEquals(0x40603C00, ml[7].int1, "7.int1")
        assertEquals(0x10000000, ml[7].int2, "7.int2")
    }

    @Test
    fun midi2PitchbendRel() {
        val mml = """
1   o5 c0,1 B=0 r4 B=-8192 r4 B+4096 r4 B-3072
"""
        val umpx = MmlTestUtility.testCompile2("midi2", mml).toList()
        val music = Midi2Music().apply { read(umpx) }
        val ml = music.tracks[0].messages
        assertEquals(0x40E00000, ml[1].int1, "1.int1")
        assertEquals(0x80000000.toInt(), ml[1].int2, "1.int2")
        assertEquals(0x40E00000, ml[3].int1, "3.int1")
        assertEquals(0x00000000, ml[3].int2, "3.int2")
        assertEquals(0x40E00000, ml[5].int1, "5.int1")
        assertEquals(0x40000000, ml[5].int2, "5.int2")
        assertEquals(0x40E00000, ml[7].int1, "7.int1")
        assertEquals(0x10000000, ml[7].int2, "7.int2")
    }

    @Test
    fun gateTime() {
        val mml = """
1   c4 q4 c4 Q4 c4 q0 c4 l12 c Q8 c
"""
        val smf = MmlTestUtility.testCompile("mml1", mml).toList()
        val music = MidiMusic().apply { read(smf) }
        val ml = music.tracks[0].messages
        assertEquals(MidiChannelStatus.NOTE_OFF, ml[1].event.eventType.toUnsigned(), "eventType1")
        assertEquals(48, ml[1].deltaTime, "deltaTime1")
        assertEquals(MidiChannelStatus.NOTE_OFF, ml[3].event.eventType.toUnsigned(), "eventType2")
        assertEquals(44, ml[3].deltaTime, "deltaTime2")
        assertEquals(MidiChannelStatus.NOTE_OFF, ml[5].event.eventType.toUnsigned(), "eventType3")
        assertEquals(20, ml[5].deltaTime, "deltaTime3")
        assertEquals(MidiChannelStatus.NOTE_OFF, ml[7].event.eventType.toUnsigned(), "eventType4")
        assertEquals(24, ml[7].deltaTime, "deltaTime4")
        assertEquals(MidiChannelStatus.NOTE_OFF, ml[9].event.eventType.toUnsigned(), "eventType5")
        assertEquals(8, ml[9].deltaTime, "deltaTime5")
        assertEquals(MidiChannelStatus.NOTE_OFF, ml[11].event.eventType.toUnsigned(), "eventType6")
        assertEquals(16, ml[11].deltaTime, "deltaTime6")
    }

    @Test
    fun chordAndGateTime() {
        // context: https://github.com/atsushieno/mugene-ng/issues/21
        val mml = """
1   q1 c0e4c4
"""
        val smf = MmlTestUtility.testCompile("mml1", mml).toList()
        val music = MidiMusic().apply { read(smf) }
        val ml = music.tracks[0].messages
        assertEquals(MidiChannelStatus.NOTE_ON, ml[0].event.eventType.toUnsigned(), "smf: note-on should appear")

        val umpx = MmlTestUtility.testCompile2("mml1", mml).toList()
        val music2 = Midi2Music().apply { read(umpx) }
        val ml2 = music2.tracks[0].messages
        assertEquals(MidiChannelStatus.NOTE_ON, ml2[0].eventType, "umpx: note-on should appear")
    }

    // FIXME: enable this once issue #15 gets fixed
    //@Test
    fun noteOffThenOnPrioritization() {
        // context: https://github.com/atsushieno/mugene-ng/issues/15
        val mml = """
1   l8 ceg r-4. egb
"""
        val reports = mutableListOf<String>()
        val smf = MmlTestUtility.testCompile("mml1", mml, reporter = {  _, _, message -> reports.add(message) }).toList()
        assertEquals(0, reports.size, "reported: " + reports.firstOrNull())
        val music = MidiMusic().apply { read(smf) }
        var current = 0
        val notes = mutableMapOf<Byte,Int>()
        var count = 0
        music.tracks[0].messages.forEach {
            count++
            current += it.deltaTime
            when (it.event.eventType.toUnsigned()) {
                MidiChannelStatus.NOTE_OFF -> {
                    val existing = notes[it.event.msb]
                    assertNotNull(existing)
                    assertTrue(current != existing, "note on and off at the same time == zero length: " + it.event.msb)
                    notes.remove(it.event.msb)
                }
                MidiChannelStatus.NOTE_ON -> {
                    assertTrue(notes[it.event.msb] == null, "There is already an existing note on: " + it.event.msb)
                    notes[it.event.msb] = current
                }
            }
        }
        assertEquals(13, count, "event count")
    }

    @Test
    fun compileLargeMml() {
        val music = MmlCompilerConsole.create().compile(listOf("../samples/mars.mugene", "--midi2x"))
    }
}
