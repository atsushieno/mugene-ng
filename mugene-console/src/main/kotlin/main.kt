import dev.atsushieno.mugene.MmlCompilerJvm
import dev.atsushieno.mugene.MmlException

fun main(args: Array<String>) {
    try {
        MmlCompilerJvm().compile(args.toList())
    } catch (ex: MmlException) {
        println(ex.message)
    }
}