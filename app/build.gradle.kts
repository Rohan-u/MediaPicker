plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.app.mediapicker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.mediapicker"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)

    // SDP SSP For Text size or margin for layout
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    // Data binding
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.play.services.cast.framework)
    implementation(project(":mediapickerlibrary"))
}