package dev.atsushieno.mugene

private external fun require(module: String): dynamic
private val fs = if(js("typeof(process) !== 'undefined'") as Boolean) require("fs") else null
private val path = if(js("typeof(process) !== 'undefined'") as Boolean) require("path") else null

//region input sources to tokenizer sources

open class LocalFileStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        val abs = if (baseSourcePath != null) path.resolve(baseSourcePath, file)  as? String else file
        if (!includes.any()) {
            if (!fs.existsSync(abs) as Boolean)
                return abs
            return fs.realpathSync(abs, options = "").toString()
        }
        if (fs.existsSync(abs) as Boolean && fs.realpathSync(abs, options = "").toString() == abs)
            return abs
        return fs.realpathSync(includes.last() + '/' + file, options = "").toString()
    }

    override fun onGetEntity(resolvedFilePath: String): String? {
        if (fs.existsSync(resolvedFilePath) as Boolean)
            return fs.readFileSync(resolvedFilePath, options = "")?.toString("utf-8") as String?
        return null
    }
}

// I assume this only works for dev. environment for unit testing. We need more paths to resolve...
class JsDevResourceStreamResolver : LocalFileStreamResolver() {

    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        val p = if (path.isAbsolute(file) as Boolean) file else "../../../../mugene/build/processedResources/js/main/$file"
        return super.resolveFilePath(p, baseSourcePath)
    }
}

@JsExport
fun setNodeModuleResourceStreamResolverBasePath(basePath: String) {
    NodeModuleResourceStreamResolver.instance.basePath = basePath
}

class NodeModuleResourceStreamResolver(var basePath: String) : LocalFileStreamResolver() {

    companion object {
        val instance = NodeModuleResourceStreamResolver(".")
    }

    override fun resolveFilePath(file: String, baseSourcePath: String?): String? {
        return super.resolveFilePath("$basePath/$file", baseSourcePath)
    }
}
