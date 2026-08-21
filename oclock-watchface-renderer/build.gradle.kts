@file:Suppress("UnstableApiUsage")

plugins {
    id("android-lib")
    kotlin("plugin.compose")
    id("maven-publish-lib")
}

group = "org.splitties.compose.oclock"
version = "0.3.0"

android {
    namespace = "org.splitties.compose.oclock.renderer"

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
    api(project(":oclock-core"))
    api {
        AndroidX.compose.ui()
        AndroidX.wear.watchFace()
    }
    api("androidx.wear.watchface:watchface-complications-data:1.2.1")
    api("androidx.wear.watchface:watchface-complications-rendering:1.2.1")
    implementation {
        AndroidX.lifecycle.runtime.ktx()
    }
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)
}
