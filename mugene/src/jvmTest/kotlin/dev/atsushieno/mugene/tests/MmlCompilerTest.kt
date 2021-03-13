
package dev.atsushieno.mugene.tests

import kotlin.test.Test

class MmlCompilerTest {
    @Test
    fun simpleCompilation() {
        MmlTestUtility.testCompile ("SimpleCompilation", "1   o5cde")
    }
}