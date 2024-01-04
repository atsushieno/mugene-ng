import dev.atsushieno.mugene.MmlCompilerNative
import dev.atsushieno.mugene.MmlException

fun main(args: Array<String>) {
    try {
        MmlCompilerNative().compile(args.toList())
    } catch (ex: MmlException) {
        println(ex.message)
    }
}
