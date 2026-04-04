import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("countries.android.library")
}

android {
    namespace = "com.github.leanite.countries.core.domain"
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findLibrary("javax-inject").get())
    implementation(libs.findLibrary("kotlinx-coroutines-android").get())

    testImplementation(libs.findLibrary("junit").get())
    testImplementation(libs.findLibrary("mockk").get())
    testImplementation(libs.findLibrary("kotlinx-coroutines-test").get())
}
