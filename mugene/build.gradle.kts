buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("dev.atsushieno.antlr-kotlin:antlr-kotlin-gradle-plugin:0.0.8")
    }
}

plugins {
    id("com.android.library") version "4.1.3"
    kotlin("multiplatform") version "1.5.21"
    id("dev.petuska.npm.publish") version "2.0.3"
    id("maven-publish")
    id("signing")
}

group = "dev.atsushieno"
version = "0.2.15"

val ktmidi_version = "0.3.8"

kotlin {
    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishLibraryVariants("debug", "release")
    }
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(LEGACY) {
        binaries.executable()
        nodejs {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
            useCommonJs()
        }
        //browser()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

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
                implementation("dev.atsushieno:ktmidi:$ktmidi_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
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
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.startup:startup-runtime:1.1.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting {
            dependencies {
            }
        }
        val nativeTest by getting
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
tasks.getByName("compileKotlinJs").dependsOn("generateKotlinCommonGrammarSource")
//tasks.getByName("compileKotlinJsIr").dependsOn("generateKotlinCommonGrammarSource")
//tasks.getByName("compileKotlinJsLegacy").dependsOn("generateKotlinCommonGrammarSource")
tasks.getByName("compileKotlinMetadata").dependsOn("generateKotlinCommonGrammarSource")
afterEvaluate {
    tasks.getByName("compileDebugKotlinAndroid").dependsOn("generateKotlinCommonGrammarSource")
    tasks.getByName("compileReleaseKotlinAndroid").dependsOn("generateKotlinCommonGrammarSource")
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.srcDir("src/commonMain/resources") // kind of hack...
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(30)
    }
    buildTypes {
        val debug by getting {
            minifyEnabled(false)
        }
        val release by getting {
            minifyEnabled(false)
        }
    }
}

afterEvaluate {
    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    publishing {

        publications.withType<MavenPublication> {

            artifact(javadocJar)

            pom {
                name.set("mugene-ng")
                description.set("mugene-ng MML (Music Macro Language) compiler for MIDI 1.0 and MIDI 2.0")
                url.set("https://github.com/atsushieno/mugene-ng")
                scm {
                    url.set("https://github.com/atsushieno/mugene-ng")
                }
                licenses {
                    license {
                        name.set("the MIT License")
                        url.set("https://github.com/atsushieno/mugene-ng/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("atsushieno")
                        name.set("Atsushi Eno")
                        email.set("atsushieno@gmail.com")
                    }
                }
            }
        }

        val ver = version.toString()
        for (p in publications) {
            (p as MavenPublication).apply {
                groupId = group.toString()
                if (name.contains("kotlinMultiplatform")) {
                    artifactId = "mugene"
                } else if (name.contains("android")) {
                    artifactId = "mugene-android"
                } else {
                    artifactId = "mugene-${name}"
                }
                version = ver
            }
        }
    }

    // keep it as is. It is replaced by CI release builds
    signing {}
}

apply(from = "${rootDir}/publish-npm.gradle")
