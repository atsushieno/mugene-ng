package dev.atsushieno.mugene

//region input sources to tokenizer sources

class LocalFileStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String): String? {
        if (!includes.any())
            return fs.realpathSync(file, options = "").toString()
        if (fs.realpathSync(file, options = "").toString() == file)
            return file
        return fs.realpathSync(includes.last() + '/' + file, options = "").toString()
    }

    override fun onGetEntity(file: String): String? {
        val abs = resolveFilePath(file)!!
        if (fs.existsSync(abs))
            return fs.readFileSync(abs, options = "")
        return null
    }
}

class JsResourceStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String): String? {
        TODO("Not implemented")
        /*
        val resName = if (file.startsWith("/")) file else "/$file"
        val res = javaClass.getResource(resName)
        if (res == null)
            return null
        return resName
        */
    }

    override fun onGetEntity(file: String): String? {
        TODO("Not implemented")
        /*
        val resName = resolveFilePath(file) ?: return null
        javaClass.getResource(resName).openStream().use {
            return java.io.InputStreamReader(it).readText()
        }
        */
    }
}
