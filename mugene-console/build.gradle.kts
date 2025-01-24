import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("application")
}

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun { mainClass = "MainKt" }
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser { binaries.executable() }
        nodejs { binaries.executable() }
    }
    js {
        browser { binaries.executable() }
        nodejs { binaries.executable() }
    }
    listOf(
        linuxArm64(),
        linuxX64(),
        mingwX64(),
    ).forEach { it.binaries.executable() }
    if (Os.isFamily(Os.FAMILY_MAC)) {
        listOf(
            macosArm64(),
            macosX64(),
            iosArm64(),
            iosSimulatorArm64(),
            iosX64(),
        ).forEach { it.binaries.executable() }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":mugene"))
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
