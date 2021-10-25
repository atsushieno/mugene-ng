package dev.atsushieno.mugene

import java.io.File
import java.io.FileReader
import java.nio.file.Path

//region input sources to tokenizer sources

class LocalFileStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        val abs = if (baseSourcePath != null && File(baseSourcePath).exists())
            Path.of(baseSourcePath).resolve(file).toString() else file
        if (!includes.any())
            return File(abs).absolutePath
        if (File(abs).exists())
            return abs
        return File(includes.last(), abs).absolutePath
    }

    override fun onGetEntity(resolvedPath: String): String? {
        if (File(resolvedPath).exists())
            return FileReader(resolvedPath).readText()
        return null
    }
}

class JarResourceStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        val resName = if (file.startsWith("/")) file else "/$file"
        val resolved = if (baseSourcePath != null) Path.of(baseSourcePath).resolve(file).toString() else resName
        return if (javaClass.getResource(resolved) != null) resolved
            else if (javaClass.getResource(resName) != null) resName
            else null
    }

    override fun onGetEntity(resolvedPath: String): String? {
        javaClass.getResource(resolvedPath).openStream().use {
            return java.io.InputStreamReader(it).readText()
        }
    }
}
