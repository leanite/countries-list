import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    id("countries.android.application")
    id("countries.android.compose")
    id("countries.android.hilt")
    alias(libs.plugins.secrets.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.github.leanite.countries"

    defaultConfig {
        applicationId = "com.github.leanite.countries"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    secrets {
        propertiesFileName = "local.properties"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    // Modules
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-data"))
    implementation(project(":core:core-ui"))
    implementation(project(":feature:feature-list"))
    implementation(project(":feature:feature-details"))

    // AndroidX
    implementation(libs.findLibrary("androidx-core-ktx").get())
    implementation(libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
    implementation(libs.findLibrary("androidx-activity-compose").get())
    implementation(libs.findLibrary("androidx-navigation3-runtime").get())
    implementation(libs.findLibrary("androidx-navigation3-ui").get())
    implementation(libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())
    implementation(libs.findLibrary("kotlinx-serialization-json").get())

    // Coil
    implementation(libs.findLibrary("coil-core").get())
    implementation(libs.findLibrary("coil-svg").get())

    // Testing
    testImplementation(libs.findLibrary("junit").get())
    androidTestImplementation(libs.findLibrary("androidx-junit").get())
    androidTestImplementation(libs.findLibrary("androidx-espresso-core").get())
    androidTestImplementation(platform(libs.findLibrary("androidx-compose-bom").get()))
    androidTestImplementation(libs.findLibrary("androidx-compose-ui-test-junit4").get())
}