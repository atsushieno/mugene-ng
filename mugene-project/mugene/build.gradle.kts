buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("dev.atsushieno.antlr-kotlin:antlr-kotlin-gradle-plugin:0.0.10")
    }
}

plugins {
    id("com.android.library") version "7.4.2"
    kotlin("multiplatform") version "1.8.20"
    id("dev.petuska.npm.publish") version "2.1.2"
    id("maven-publish")
    id("signing")
}

group = "dev.atsushieno"
version = "0.4.0"

val ktmidi_version = "0.5.0"

kotlin {
    jvmToolchain(11)

    android {
        publishLibraryVariantsGroupedByFlavor = true
        publishLibraryVariants("debug", "release")
    }
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(IR) {
        binaries.library() // binaries.executable() did not work, results in empty package.
        useCommonJs()
        nodejs {
            testTask {
                useKarma {
                    useChromeHeadless()
                    //webpackConfig.cssSupport.enabled = true
                }
            }
        }
        browser()
    }
    macosArm64()
    macosX64()
    // we could not build it in ktmidi, due to lack of linuxArm64 version of kotlinx-datetime 0.4.0
    //linuxArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonAntlr by creating {
            dependencies {
                api("dev.atsushieno.antlr-kotlin:antlr-kotlin-runtime:0.0.10")
            }
            kotlin.srcDir("build/generated-src/commonAntlr/kotlin")
        }
        val commonMain by getting {
            dependencies {
                implementation("dev.atsushieno:ktmidi:$ktmidi_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
            }
            dependsOn(commonAntlr)
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.startup:startup-runtime:1.1.1")
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
                implementation(npm("fs", ""))
                implementation(npm("buffer", ""))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        // call to linuxArm64() is commented out
        //val linuxArm64Main by getting {
        //    dependsOn(nativeMain)
        //}
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        val appleMain by creating {
            dependsOn(nativeMain)
        }
        val macosArm64Main by getting {
            dependsOn(appleMain)
        }
        val macosX64Main by getting {
            dependsOn(appleMain)
        }
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
        project.dependencies.create("dev.atsushieno.antlr-kotlin:antlr-kotlin-target:0.0.10")
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
val generateGrammarTask = tasks.getByName("generateKotlinCommonGrammarSource")
// It is kind of hack, but it's rather error-prone to manually specify *everything* here
// (can you notice that you missed `compileKotlinLinuxArm64Metadata` ?)
tasks.filter { it.name.startsWith("compileKotlin") and !it.name.contains("KotlinAndroid") }.forEach {
    it.dependsOn(generateGrammarTask)
}
tasks.filter { it.name.endsWith("ourcesJar") and !it.name.contains("Android") }.forEach {
    it.dependsOn(generateGrammarTask)
}
afterEvaluate {
    tasks.filter { it.name.matches(Regex("compile.*KotlinAndroid")) }.forEach {
        it.dependsOn(generateGrammarTask)
    }
    tasks.filter { it.name.matches(Regex("android.*SourcesJar")) }.forEach {
        it.dependsOn(generateGrammarTask)
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.srcDir("src/commonMain/resources") // kind of hack...
    defaultConfig {
        minSdk = 23
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

        repositories {
            maven {
                name = "OSSRH"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }

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
