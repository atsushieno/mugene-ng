package dev.atsushieno.mugene


fun main(args: Array<String>) {
    try {
        MmlCompilerJvm().compile(args.toList())
    } catch (ex: MmlException) {
        println(ex.message)
    }
}
