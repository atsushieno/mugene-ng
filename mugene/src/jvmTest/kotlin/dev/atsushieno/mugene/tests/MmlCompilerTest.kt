
package dev.atsushieno.mugene.tests

import kotlin.test.Test

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
        MmlTestUtility.testCompile("arguments", "#variable __octave:number = 5\n#macro o val:number { __LET{\"__octave\", \$val} }\n1   o5", true)
    }

    @Test
    fun negativeVariableBinding() {
        MmlTestUtility.testCompile("arguments", "#variable __trans_c:number = 0\n#macro Kc- { __LET{\"__trans_c\", -1} }", true)
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
        val actual = MmlTestUtility.testCompile("SimpleCompilation", "1   o5cde")
        val expected = intArrayOf(
            'M'.toInt(), 'T'.toInt(), 'h'.toInt(), 'd'.toInt(), 0, 0, 0, 6, 0, 1, 0, 1, 0, 0x30,
            'M'.toInt(), 'T'.toInt(), 'r'.toInt(), 'k'.toInt(), 0, 0, 0, 0x1B,
            0, 0x90, 0x3B, 100,
            0x30, 0x80, 0x3B, 0,
            0, 0x90, 0x3D, 100,
            0x30, 0x80, 0x3D, 0,
            0, 0x90, 0x3F, 100,
            0x30, 0x80, 0x3F, 0,
            0, 0xFF, 0x2F).map { i -> i.toByte() }.toByteArray()
        assertArrayEquals(expected.toTypedArray(), actual.toTypedArray(), "MIDI bytes")
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
        MmlTestUtility.testCompile("Macro definition", "#macro\tCH_INIT ch:number { CH\$ch E127 B0 P64 M0 H0 RSD0 CSD0 DSD0 v80 l8 q16 }")
    }

    @Test
    fun metaTitle() {
        MmlTestUtility.testCompile("meta","#meta title \"test\"")
    }
}