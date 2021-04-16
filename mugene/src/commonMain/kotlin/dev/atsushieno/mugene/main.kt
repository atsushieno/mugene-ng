package dev.atsushieno.mugene

fun main(args: Array<String>) {
    try {
        MmlCompilerConsole.create().compile(args.toList())
    } catch (ex: MmlException) {
        println(ex.message)
    }
}
