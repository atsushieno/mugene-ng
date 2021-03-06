plugins {
    kotlin("multiplatform") version "1.5.21"
}

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native") { // on macOS
            binaries.executable {
                freeCompilerArgs += "-Xdisable-phases=EscapeAnalysis"
            }
        }
        hostOs == "Linux" ->  linuxX64("native") { // on Linux
            binaries.executable {
                freeCompilerArgs += "-Xdisable-phases=EscapeAnalysis"
            }
        }
        isMingwX64 -> mingwX64("native") { // on Windows
            binaries.executable {
                freeCompilerArgs += "-Xdisable-phases=EscapeAnalysis"
            }
        }
        else -> {}
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(project(":mugene"))
            }
        }
    }
}

// LAMESPEC: native resources are not copied, including those from dependencies.
//  https://youtrack.jetbrains.com/issue/KT-29311
tasks {
    val sources = arrayOf("../mugene/build/processedResources/native/main",
        "../mugene/build/processedResources/apple/main",
        "../mugene/build/processedResources/mingwX64/main",
        "../mugene/build/processedResources/linuxX64/main")
    val copyDebugResource by registering(Copy::class) {
        configurations.forEach {
            from(sources)
            into("build/bin/native/debugExecutable/")
        }
    }
    val copyReleaseResource by registering(Copy::class) {
        configurations.forEach {
            from(sources)
            into("build/bin/native/releaseExecutable/")
        }
    }
    build {
        dependsOn(copyDebugResource)
        dependsOn(copyReleaseResource)
    }
}

tasks.withType<Wrapper> {
  gradleVersion = "7.1.1"
  distributionType = Wrapper.DistributionType.BIN
}
