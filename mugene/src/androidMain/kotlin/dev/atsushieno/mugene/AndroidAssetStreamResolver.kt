package dev.atsushieno.mugene

import android.content.res.AssetManager
import java.io.InputStreamReader

class AndroidAssetStreamResolver(private var assets: AssetManager) : StreamResolver() {

    // can resolve only assets
    override fun resolveFilePath(file: String, baseSourcePath: String?): String? =
        if (android.os.Build.VERSION.SDK_INT >= 26 && baseSourcePath != null)
            java.nio.file.Paths.get(baseSourcePath).resolve(file).toString()
        else
            file

    override fun onGetEntity(file: String): String? {
        assets.open(file).use { res ->
            InputStreamReader(res).use {
                return it.readText()
            }
        }
    }
}

