package dev.atsushieno.mugene

import java.io.File
import java.io.FileReader

//region input sources to tokenizer sources

class LocalFileStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String): String? {
        if (!includes.any())
            return File(file).absolutePath
        if (File(file).isAbsolute)
            return file
        return File(includes.last(), file).absolutePath
    }

    override fun onGetEntity(file: String): String? {
        val abs = resolveFilePath(file)
        if (File(abs).exists())
            return FileReader(abs).readText()
        return null
    }
}

class JarResourceStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String): String? {
        val resName = if (file.startsWith("/")) file else "/$file"
        val res = javaClass.getResource(resName)
        if (res == null)
            return null
        return resName
    }

    override fun onGetEntity(file: String): String? {
        val resName = resolveFilePath(file) ?: return null
        javaClass.getResource(resName).openStream().use {
            return java.io.InputStreamReader(it).readText()
        }
    }
}
