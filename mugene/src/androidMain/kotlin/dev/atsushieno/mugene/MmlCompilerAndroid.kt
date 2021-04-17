package dev.atsushieno.mugene

import android.content.Context
import android.content.res.AssetManager

class MmlCompilerAndroid(assets: AssetManager) : MmlCompiler() {
    override var resolver: StreamResolver = AndroidAssetStreamResolver(assets)
}

// Developers are supposed to set this property before trying to create any default MML compiler.
lateinit var applicationContextForDefaultCompiler: Context

internal actual fun createDefaultCompiler() : MmlCompiler = MmlCompilerAndroid(applicationContextForDefaultCompiler!!.assets)
