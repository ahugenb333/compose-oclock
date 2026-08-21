@file:Suppress("UnstableApiUsage")

plugins {
    id("android-lib")
    kotlin("plugin.compose")
    id("maven-publish-lib")
}

group = "org.splitties.compose.oclock"
version = "0.3.0"

android {
    namespace = "org.splitties.compose.oclock"

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-opt-in=org.splitties.compose.oclock.ExperimentalComposeOClockApi",
            "-opt-in=org.splitties.compose.oclock.internal.InternalComposeOClockApi",
        )
    }
}

dependencies {
    api {
        AndroidX.compose.runtime()
        AndroidX.compose.ui()
        AndroidX.compose.ui.graphics()
        AndroidX.compose.foundation()
        AndroidX.core.ktx()
        AndroidX.lifecycle.runtime.ktx()
        AndroidX.wear.watchFace()
    }
    api("androidx.wear.watchface:watchface-complications-data:1.2.1")
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)
}
