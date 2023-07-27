plugins {
    kotlin("multiplatform")
}

kotlin {
    macosArm64()
    macosX64()
    // we could not build it in ktmidi, due to lack of linuxArm64 version of kotlinx-datetime 0.4.0
    //linuxArm64()
    linuxX64()
    mingwX64()
    sourceSets {
        val nativeMain by creating {
            dependencies {
                implementation(libs.mugene)
            }
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
