pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "mugene-project"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("mugene")
include("mugene-console")
include("mugene-console-jvm")
