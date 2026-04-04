plugins {
    id("countries.android.library")
    id("countries.android.compose")
}

android {
    namespace = "com.github.leanite.countries.core.bottomsheet"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:core-ui"))

    implementation(libs.findLibrary("androidx-core-ktx").get())

    testImplementation(libs.findLibrary("junit").get())

    androidTestImplementation(platform(libs.findLibrary("androidx-compose-bom").get()))
    androidTestImplementation(libs.findLibrary("androidx-compose-ui-test-junit4").get())
    androidTestImplementation(libs.findLibrary("androidx-junit").get())
    androidTestImplementation(libs.findLibrary("androidx-espresso-core").get())

    debugImplementation(libs.findLibrary("androidx-compose-ui-test-manifest").get())
}
