buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("dev.atsushieno.antlr-kotlin:antlr-kotlin-gradle-plugin:0.0.8")
    }
}

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
        val commonAntlr by creating {
            dependencies {
                api(kotlin("stdlib-common"))
                api("dev.atsushieno.antlr-kotlin:antlr-kotlin-runtime:0.0.8")
            }
            kotlin.srcDir("build/generated-src/commonAntlr/kotlin")
        }
        val commonMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi-kotlinMultiplatform:0.1.10")
            }
            dependsOn(commonAntlr)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi-jvm:0.1.10")
                implementation("dev.atsushieno:ktmidi-jvm-desktop:0.1.10")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi-js:0.1.10")
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
                implementation("dev.atsushieno:ktmidi-native:0.1.9")
            }
        }
        val nativeTest by getting
*/
    }
}


// copying(2) antlr-kotlin mpp example

// in antlr-kotlin-plugin <0.0.5, the configuration was applied by the plugin.
// starting from verison 0.0.5, you have to apply it manually:
tasks.register<com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask>("generateKotlinCommonGrammarSource") {
    // the classpath used to run antlr code generation
    antlrClasspath = configurations.detachedConfiguration(
        // antlr itself
        // antlr is transitive added by antlr-kotlin-target,
        // add another dependency if you want to choose another antlr4 version (not recommended)
        // project.dependencies.create("org.antlr:antlr4:$antlrVersion"),

        // antlr target, required to create kotlin code
        project.dependencies.create("dev.atsushieno.antlr-kotlin:antlr-kotlin-target:0.0.8")
    )
    maxHeapSize = "64m"
    packageName = "dev.atsushieno.mugene.parser"
    arguments = listOf("-listener", "-visitor")
    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("src/commonAntlr/antlr").apply {
            include("*.g4")
        }
    // outputDirectory is required, put it into the build directory
    // if you do not want to add the generated sources to version control
    outputDirectory = File("build/generated-src/commonAntlr/kotlin")
    // use this settings if you want to add the generated sources to version control
    // outputDirectory = File("src/commonAntlr/kotlin")
}

// run generate task before build
// not required if you add the generated sources to version control
// you can call the task manually in this case to update the generated sources
tasks.getByName("compileKotlinJvm").dependsOn("generateKotlinCommonGrammarSource")

// end of copy(2)

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

