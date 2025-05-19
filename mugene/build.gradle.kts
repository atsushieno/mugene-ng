import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.npmPublish)
    alias(libs.plugins.dokka)
    alias(libs.plugins.antlrKotlinPlugin)
    alias(libs.plugins.vanniktech.maven.publish)
}

group = "dev.atsushieno"
version = libs.versions.mugene.get()

kotlin {
    jvmToolchain(17)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.add("kotlin.ExperimentalStdlibApi")
    }

    androidTarget {
        publishLibraryVariantsGroupedByFlavor = true
        publishLibraryVariants("debug", "release")
    }
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        binaries.library()
        browser()
        nodejs()
    }
    js {
        binaries.library()
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
    if (Os.isFamily(Os.FAMILY_MAC)) {
        macosArm64()
        macosX64()

        listOf(
            iosArm64(),
            iosSimulatorArm64(),
            iosX64()
        ).onEach {
            it.binaries {
                framework { baseName = "mugene" }
            }
        }
    }
    linuxArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.antlr.kotlin.runtime)
                implementation(libs.ktmidi)
                implementation(libs.kotlinx.coroutines.core)
                //implementation(libs.ktor.io)
                implementation(libs.antlr.kotlin.runtime)
            }
            kotlin.srcDir(layout.buildDirectory.dir("generatedAntlr"))
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
                implementation(libs.startup.runtime)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
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
        /*
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val linuxCommonMain by creating {
            dependsOn(nativeMain)
        }
        val linuxArm64Main by getting {
            dependsOn(linuxCommonMain)
        }
        val linuxX64Main by getting {
            dependsOn(linuxCommonMain)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        if (Os.isFamily(Os.FAMILY_MAC)) {
            val appleMain by creating {
                dependsOn(nativeMain)
            }
            val macosMain by creating {
                dependsOn(appleMain)
            }
            val macosArm64Main by getting { dependsOn(macosMain) }
            val macosX64Main by getting { dependsOn(macosMain) }
            val iosMain by creating { dependsOn(appleMain) }
            val iosArm64Main by getting { dependsOn(iosMain) }
            val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
            val iosX64Main by getting { dependsOn(iosMain) }
        }*/
    }
}

// copying antlr-kotlin README

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
    dependsOn("cleanGenerateKotlinGrammarSource")

    // ANTLR .g4 files are under {example-project}/antlr
    // Only include *.g4 files. This allows tools (e.g., IDE plugins)
    // to generate temporary files inside the base path
    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("src/commonAntlr/antlr").apply {
            include("*.g4")
        }

    // We want the generated source files to have this package name
    val pkgName = "dev.atsushieno.mugene.parser"
    packageName = pkgName

    // We want visitors alongside listeners.
    // The Kotlin target language is implicit, as is the file encoding (UTF-8)
    arguments = listOf("-visitor")

    // Generated files are outputted inside build/generatedAntlr/{package-name}
    val outDir = "generatedAntlr/${pkgName.replace(".", "/")}"
    outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}

tasks.withType<KotlinCompilationTask<*>> { dependsOn(generateKotlinGrammarSource) }
afterEvaluate {
    tasks.findByPath(":mugene:androidDebugSourcesJar") !!.dependsOn(generateKotlinGrammarSource)
    tasks.findByPath(":mugene:androidReleaseSourcesJar")!!.dependsOn(generateKotlinGrammarSource)
}
tasks.filter { it.name.endsWith("ourcesJar")}.forEach {
    it.dependsOn(generateKotlinGrammarSource)
}

android {
    namespace = "dev.atsushieno.mugene"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.srcDir("src/commonMain/resources") // kind of hack...
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    buildTypes {
        val debug by getting
        val release by getting
    }
}

apply(from = "${rootDir}/publish-npm.gradle")

val gitProjectName = "mugene-ng"
val packageName = project.name
val packageDescription = "mugene-ng MML (Music Macro Language) compiler for MIDI 1.0 and MIDI 2.0"
// my common settings
val packageUrl = "https://github.com/atsushieno/$gitProjectName"
val licenseName = "MIT"
val licenseUrl = "https://github.com/atsushieno/$gitProjectName/blob/main/LICENSE"
val devId = "atsushieno"
val devName = "Atsushi Eno"
val devEmail = "atsushieno@gmail.com"

// Common copy-pasted
mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    if (project.hasProperty("mavenCentralUsername") || System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername") != null)
        signAllPublications()
    coordinates(group.toString(), project.name, version.toString())
    pom {
        name.set(packageName)
        description.set(packageDescription)
        url.set(packageUrl)
        scm { url.set(packageUrl) }
        licenses { license { name.set(licenseName); url.set(licenseUrl) } }
        developers { developer { id.set(devId); name.set(devName); email.set(devEmail) } }
    }
}
