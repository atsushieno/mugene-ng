package dev.atsushieno.mugene

import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class AndroidAssetStreamResolver(var assets: AssetManager) : StreamResolver() {
    private val openedAssets = mutableListOf<InputStream>()

    override fun onGetEntity(file: String): Reader {
        var res = assets.open(file)
        openedAssets.add(res)
        return InputStreamReader(res)
    }
}