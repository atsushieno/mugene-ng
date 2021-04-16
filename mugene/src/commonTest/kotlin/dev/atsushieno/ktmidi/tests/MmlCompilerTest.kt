
package dev.atsushieno.ktmidi.tests

import dev.atsushieno.ktmidi.Midi2Music
import dev.atsushieno.ktmidi.MidiMusic
import dev.atsushieno.ktmidi.read
import dev.atsushieno.mugene.toUnsigned
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val midi2Bytes = MmlTestUtility.testCompile2("midi2", mml)
        val music2 = Midi2Music().apply { read (midi2Bytes.toList()) }
        assertEquals(72, music2.getTotalTicks(), "midi2 total ticks")
    }
}