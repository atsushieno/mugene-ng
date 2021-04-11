package dev.atsushieno.mugene


fun main(args: Array<String>) {
    try {
        MmlCompilerJs().compile(args.toList())
    } catch (ex: MmlException) {
        println(ex.message)
    }
}
