plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.10"
}

android {
    namespace = "com.cit.mycomposeapplication"
    compileSdk = 36

    defaultConfig {
//        applicationId = "com.cit.mycomposeapplication"
        applicationId = "abcdefg.a.com.cit.mycomposeapplication"
        minSdk = 21
        targetSdk = 36
        versionCode = 10000
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    // Pager (official foundation pager)
    implementation("androidx.compose.foundation:foundation:1.9.0")
// Lifecycle / ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    implementation("androidx.compose.runtime:runtime-livedata:1.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Ktor Core
    implementation("io.ktor:ktor-client-core:2.0.0")
//    implementation("io.ktor:ktor-client-cio:2.0.0")
    implementation("io.ktor:ktor-client-okhttp:2.0.0")
    
    // JSON Serialization
    implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // Play Core
    implementation("com.google.android.play:asset-delivery-ktx:2.2.2")

    // Others
    implementation("com.airbnb.android:lottie:6.4.0")
}