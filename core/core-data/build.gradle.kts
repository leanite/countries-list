import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("countries.android.library")
    id("countries.android.hilt")
    id("countries.android.data")
}

android {
    namespace = "com.github.leanite.countries.core.data"
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(project(":core:core-domain"))

    implementation(libs.findLibrary("kotlinx-coroutines-android").get())

    testImplementation(libs.findLibrary("junit").get())
    testImplementation(libs.findLibrary("mockk").get())
    testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
}
