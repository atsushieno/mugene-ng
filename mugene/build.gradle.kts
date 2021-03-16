
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
}

plugins {
    kotlin("multiplatform") version "1.4.31"
    `maven-publish`
}

group = "dev.atsushieno"
version = "0.1.0-SNAPSHOT"


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(LEGACY) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    /*
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
    */

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi-kotlinMultiplatform:0.1.2")
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi-jvm:0.1.2")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi-js:0.1.2")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        /*
        val nativeMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi-native:0.1.2")
            }
        }
        val nativeTest by getting
        */
    }
}


afterEvaluate {
    publishing {
        val ver = version.toString()
        for (p in publications) {
            (p as MavenPublication).apply {
                groupId = group.toString()
                if (name.contains("metadata")) {
                    artifactId = "mugene"
                } else {
                    artifactId = "mugene-${name}"
                }
                version = ver
            }
        }
    }
}
