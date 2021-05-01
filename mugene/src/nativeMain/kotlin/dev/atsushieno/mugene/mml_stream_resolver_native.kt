package dev.atsushieno.mugene

//region input sources to tokenizer sources

class LocalFileStreamResolver : StreamResolver() {

    override fun resolveFilePath(file: String): String? =
        TODO("Not Implemented")

    override fun onGetEntity(file: String): String? =
        TODO("Not Implemented")
}
