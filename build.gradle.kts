// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.12.0" apply false
    id("com.google.gms.google-services") version "4.4.3" apply false // Make sure this version is up-to-date
    kotlin("android") version "1.9.22" apply false
}

// Add this block if it's not already present
subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")) {
            apply(plugin = "com.google.gms.google-services")
        }
    }
}