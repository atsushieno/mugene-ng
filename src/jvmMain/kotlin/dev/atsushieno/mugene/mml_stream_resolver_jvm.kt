package dev.atsushieno.mugene

import java.io.File
import java.io.FileReader

//region input sources to tokenizer sources

class MergeStreamResolver(vararg resolvers: StreamResolver) : StreamResolver() {
    private val resolvers: MutableList<StreamResolver> = resolvers.toMutableList()

    override fun resolveFilePath(file: String): String? {
        for (r in resolvers) {
            val ret = r.resolveFilePath(file)
            if (ret != null)
                return ret
        }
        return null
    }

    override fun onGetEntity(file: String): String? {
        for (r in resolvers) {
            val ret = r.onGetEntity(file)
            if (ret != null)
                return ret
        }
        return null
    }

    override fun pushInclude(file: String) {
        for (r in resolvers)
            r.pushInclude(file)
    }

    override fun popInclude() {
        for (r in resolvers)
            r.popInclude()
    }
}

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
