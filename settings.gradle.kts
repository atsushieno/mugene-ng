pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "mugene-project"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("mugene")
include("mugene-console")
