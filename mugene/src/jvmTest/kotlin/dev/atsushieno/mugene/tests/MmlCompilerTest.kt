
package dev.atsushieno.mugene.tests

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
    fun compileNoteMacro() {
        MmlTestUtility.testCompile("note-macro", """
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
        """, true)
    }

    @Test
    fun simpleTrackAndNotes() {
        val bytes = MmlTestUtility.testCompile("SimpleCompilation", "1   o5cde")
        val expected = intArrayOf(
            'M'.toInt(), 'T'.toInt(), 'h'.toInt(), 'd'.toInt(), 0, 0, 0, 6, 0, 1, 0, 1, 0, 0x30,
            'M'.toInt(), 'T'.toInt(), 'r'.toInt(), 'k'.toInt(), 0, 0, 0, 0x1C,
            0, 0x90, 0x3C, 100,
            0x30, 0x80, 0x3C, 0,
            0, 0x90, 0x3E, 100,
            0x30, 0x80, 0x3E, 0,
            0, 0x90, 0x40, 100,
            0x30, 0x80, 0x40, 0).map { i -> i.toByte() }.toByteArray()
        assertEquals(expected, bytes, "MIDI bytes")
    }
    @Test
    fun simpleMacroDefinition() {
        MmlTestUtility.testCompile("Macro definition", "#macro\tCH_INIT ch:number { CH\$ch }")
    }
    @Test
    fun macroWithMultipleOperationUses() {
        MmlTestUtility.testCompile("Macro definition", "#macro\tCH_INIT ch:number { CH\$ch E127 }")
        MmlTestUtility.testCompile("Macro definition", "#macro\tCH_INIT ch:number { CH\$ch E127 B0 P64 M0 H0 RSD0 CSD0 DSD0 v80 l8 q16 }")
    }

    @Test
    fun metaTitle() {
        MmlTestUtility.testCompile("meta","#meta title \"test\"")
    }
}