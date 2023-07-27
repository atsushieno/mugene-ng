pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:7.4.2")
            }
        }
    }

    plugins {
        id("com.android.library") version "8.0.2" apply false
        id("org.jetbrains.kotlin.multiplatform") version "1.8.20" apply false
        id("dev.petuska.npm.publish") version "2.1.2" apply false
    }
}

rootProject.name = "mugene"
include("mugene")
