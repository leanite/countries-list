import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("countries.android.feature")
}

android {
    namespace = "com.github.leanite.countries.feature.details"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(project(":core:core-data"))

    // Maps
    implementation(libs.findLibrary("maps-compose").get())
    implementation(libs.findLibrary("play-services-maps").get())

    testImplementation(project(":core:core-testing"))

    androidTestImplementation(platform(libs.findLibrary("androidx-compose-bom").get()))
    androidTestImplementation(libs.findLibrary("androidx-compose-ui-test-junit4").get())
    androidTestImplementation(libs.findLibrary("androidx-junit").get())
    androidTestImplementation(libs.findLibrary("androidx-espresso-core").get())
}
