pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.8.20" apply false
        kotlin("multiplatform") version "1.8.20" apply false
    }
}

rootProject.name = "mugene-console-project"
include("mugene-console")
include("mugene-console-jvm")
