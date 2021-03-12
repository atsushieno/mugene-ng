package dev.atsushieno.mugene.tests

import dev.atsushieno.mugene.MmlCompiler
import dev.atsushieno.mugene.MmlInputSource
import java.io.ByteArrayOutputStream

class MmlTestUtility {
    companion object {
        fun testCompile (testLabel: String, mml: String) : ByteArray {
            val sources = mutableListOf<MmlInputSource> ()
            sources.add ( MmlInputSource ("fakefilename.mml", mml))
            val outs = ByteArrayOutputStream ()
            outs.use {
                MmlCompiler().compile(false, sources, null, outs, false)
                return outs.toByteArray()
            }
        }
    }
}