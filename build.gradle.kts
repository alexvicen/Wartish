buildscript {
    dependencies {
        // AGP 9 has built-in Kotlin; this classpath selects the project's Kotlin compiler version.
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.ksp.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
