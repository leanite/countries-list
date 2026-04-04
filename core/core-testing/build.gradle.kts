import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("countries.android.library")
}

android {
    namespace = "com.github.leanite.countries.core.testing"
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(project(":core:core-domain"))

    api(libs.findLibrary("junit").get())
    api(libs.findLibrary("mockk").get())
    api(libs.findLibrary("kotlinx-coroutines-test").get())
    api(libs.findLibrary("turbine").get())
}
