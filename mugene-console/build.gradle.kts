plugins {
    kotlin("multiplatform")
}

kotlin {
    macosArm64()
    macosX64()
    linuxArm64()
    linuxX64()
    mingwX64()
    sourceSets {
        val nativeMain by creating {
            dependencies {
                implementation(libs.mugene)
            }
        }
        val linuxArm64Main by getting {
            dependsOn(nativeMain)
        }
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
