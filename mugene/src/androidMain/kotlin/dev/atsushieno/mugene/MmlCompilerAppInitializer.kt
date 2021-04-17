package dev.atsushieno.mugene

import android.content.Context
import androidx.startup.Initializer


class MmlCompilerAppInitializer : Initializer<Unit> {
    override fun create(context: Context){
        applicationContextForDefaultCompiler = context.applicationContext
    }

    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     * <br></br>
     * For e.g. if a [Initializer] `B` defines another
     * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
     */
    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}