package dev.atsushieno.mugene

import kotlin.math.min
import kotlin.test.assertTrue

class MmlTestUtility {
    companion object {
        fun testCompile (testLabel: String, mml: String, skipDefault: Boolean = false) : ByteArray {
            val sources = mutableListOf<MmlInputSource> ()
            sources.add ( MmlInputSource ("fakefilename.mml", mml))
            val outs = mutableListOf<Byte> ()
            MmlCompiler.create().compile(skipDefault, sources, null, outs, false)
            return outs.toByteArray()
        }
        fun testCompile2 (testLabel: String, mml: String, skipDefault: Boolean = false, outputDeltaTime: Boolean = false) : ByteArray {
            val sources = mutableListOf<MmlInputSource> ()
            sources.add ( MmlInputSource ("fakefilename.mml", mml))
            val outs = mutableListOf<Byte> ()
            MmlCompiler.create().compile2(outputDeltaTime, skipDefault, sources, outs)
            return outs.toByteArray()
        }
    }
}

fun <T>assertArrayEquals(expected: Array<T> , actual: Array<T>, message: String) {
    var report = ""
    if (expected.size != actual.size)
        report += " Array size mismatch: expected: ${expected.size}, actual: ${actual.size}."
    val maxIndex = min(expected.size, actual.size)
    var mismatchCount = 0
    for(i in 0 until maxIndex) {
        if (expected[i] != actual[i]) {
            report += " Content differ at $i - expected: ${expected[i]}, actual: ${actual[i]}."
            if (mismatchCount++ > 3) // not too many report
                break
        }
    }
    assertTrue(report.isEmpty(), message + report)
}
