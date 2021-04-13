package dev.atsushieno.mugene

import android.content.res.AssetManager
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class AndroidAssetStreamResolver(private var assets: AssetManager) : StreamResolver() {

    // can resolve only assets
    override fun resolveFilePath(file: String): String? = file

    override fun onGetEntity(file: String): String? {
        assets.open(file).use { res ->
            InputStreamReader(res).use {
                return it.readText()
            }
        }
    }
}