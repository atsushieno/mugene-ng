package dev.atsushieno.mugene.tests

import dev.atsushieno.mugene.MmlCompiler
import dev.atsushieno.mugene.MmlCompilerJvm
import dev.atsushieno.mugene.MmlInputSource
import java.io.ByteArrayOutputStream
import kotlin.math.min
import kotlin.test.assertTrue

class MmlTestUtility {
    companion object {
        fun testCompile (testLabel: String, mml: String, skipDefault: Boolean = false) : ByteArray {
            val sources = mutableListOf<MmlInputSource> ()
            sources.add ( MmlInputSource ("fakefilename.mml", mml))
            val outs = ByteArrayOutputStream ()
            outs.use {
                MmlCompilerJvm().compile(skipDefault, sources, null, outs, false)
                return outs.toByteArray()
            }
        }
    }
}

fun <T>assertArrayEquals(expected: Array<T> , actual: Array<T>, message: String) {
    var report = ""
    if (expected.size != actual.size)
        report += " Array size mismatch: expected: ${expected.size}, actual: ${actual.size}"
    val maxIndex = min(expected.size, actual.size)
    var mismatchCount = 0
    for(i in 0 until maxIndex) {
        if (expected[i] != actual[i]) {
            report += " Content differ at $i - expected: ${expected[i]}, actual: ${actual[i]}"
            if (mismatchCount++ > 3) // not too many report
                break
        }
    }
    assertTrue(report.isNotEmpty(), report)
}
