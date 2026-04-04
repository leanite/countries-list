import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("countries.android.library")
    id("countries.android.compose")
}

android {
    namespace = "com.github.leanite.countries.core.ui"
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findLibrary("androidx-core-ktx").get())

    // Maps
    api(libs.findLibrary("maps-compose").get())
    api(libs.findLibrary("play-services-maps").get())
}
