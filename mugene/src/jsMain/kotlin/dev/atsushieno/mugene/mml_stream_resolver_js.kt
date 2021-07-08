package dev.atsushieno.mugene

private external fun require(module: String): dynamic
private val fs = if(js("typeof(process) !== 'undefined'") as Boolean) require("fs") else null

//region input sources to tokenizer sources

open class LocalFileStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String): String? {
        if (!includes.any()) {
            if (!fs.existsSync(file) as Boolean)
                return file
            return fs.realpathSync(file, options = "").toString()
        }
        if (fs.existsSync(file) as Boolean && fs.realpathSync(file, options = "").toString() == file)
            return file
        return fs.realpathSync(includes.last() + '/' + file, options = "").toString()
    }

    override fun onGetEntity(file: String): String? {
        val abs = resolveFilePath(file)!!
        if (fs.existsSync(abs) as Boolean)
            return fs.readFileSync(abs, options = "")?.toString()
        return null
    }
}

// I assume this only works for dev. environment for unit testing. We need more paths to resolve...
class JsDevResourceStreamResolver : LocalFileStreamResolver() {

    override fun resolveFilePath(file: String): String? {
        return super.resolveFilePath("../../../../mugene/build/processedResources/js/main/$file")
    }
}

class NodeModuleResourceStreamResolver(var basePath: String) : LocalFileStreamResolver() {

    companion object {
        val instance = NodeModuleResourceStreamResolver(".")
    }

    override fun resolveFilePath(file: String): String? {
        return super.resolveFilePath("$basePath/$file")
    }
}
