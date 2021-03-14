
package dev.atsushieno.mugene.tests

import kotlin.test.Test
import kotlin.test.assertEquals

class MmlCompilerTest {
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