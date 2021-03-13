
package dev.atsushieno.mugene.tests

import kotlin.test.Test

class MmlCompilerTest {
    @Test
    fun simpleCompilation() {
        MmlTestUtility.testCompile("SimpleCompilation", "1   o5cde")
        MmlTestUtility.testCompile("Macro definition", "#macro\tCH_INIT ch:number { CH\$ch E127 B0 P64 M0 H0 RSD0 CSD0 DSD0 v80 l8 q16 }")
    }
}