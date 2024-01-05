import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

buildscript {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("dev.petuska.npm.publish")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
    id("com.strumenta.antlr-kotlin")
}

group = "dev.atsushieno"
version = libs.versions.mugene.get()

kotlin {
    jvmToolchain(17)

    /*
    // FIXME: remove this section once https://github.com/Strumenta/antlr-kotlin/issues/136 got fixed
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        optIn.add("kotlin.ExperimentalStdlibApi")
    }*/

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
    // FIXME: enable JS target once https://youtrack.jetbrains.com/issue/KT-62809 got fixed
    /*
    js {
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
    }*/
    //macosArm64()
    macosX64()
    linuxArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonAntlr by creating {
            dependencies {
                api(libs.antlr.kotlin.runtime)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(libs.ktmidi)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.io)
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
        val jvmMain by getting {
            dependsOn(commonAntlr)
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.startup.runtime)
            }
            dependsOn(commonAntlr)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
            }
        }
        /*
        val jsMain by getting {
            dependencies {
                implementation(npm("fs", ""))
                implementation(npm("buffer", ""))
            }
            dependsOn(commonAntlr)
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }*/
        val nativeMain by creating {
            dependsOn(commonMain)
            dependsOn(commonAntlr)
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

tasks.withType<KotlinCompile<*>> { dependsOn(generateKotlinGrammarSource) }
afterEvaluate {
    tasks.findByPath(":mugene:androidDebugSourcesJar") !!.dependsOn(generateKotlinGrammarSource)
    tasks.findByPath(":mugene:androidReleaseSourcesJar")!!.dependsOn(generateKotlinGrammarSource)
}
tasks.filter { it.name.endsWith("ourcesJar")}.forEach {
    it.dependsOn(generateKotlinGrammarSource)
}

android {
    namespace = "dev.atsushieno.mugene"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.srcDir("src/commonMain/resources") // kind of hack...
    defaultConfig {
        minSdk = 23
    }
    buildTypes {
        val debug by getting {
            //minifyEnabled(false)
        }
        val release by getting {
            //minifyEnabled(false)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

afterEvaluate {
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

            // https://github.com/gradle/gradle/issues/26091#issuecomment-1681343496
            val dokkaJar = project.tasks.register("${name}DokkaJar", Jar::class) {
                group = JavaBasePlugin.DOCUMENTATION_GROUP
                description = "Assembles Kotlin docs with Dokka into a Javadoc jar"
                archiveClassifier.set("javadoc")
                from(tasks.named("dokkaHtml"))

                // Each archive name should be distinct, to avoid implicit dependency issues.
                // We use the same format as the sources Jar tasks.
                // https://youtrack.jetbrains.com/issue/KT-46466
                archiveBaseName.set("${archiveBaseName.get()}-${name}")
            }
            artifact(dokkaJar)

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
