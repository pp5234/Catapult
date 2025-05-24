plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.catapult"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.catapult"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
    }
    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    // CORE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.core)

    // UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.animation)

    // Coli
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    ksp(libs.bundles.hilt.compiler)
    implementation(libs.bundles.hilt)

    // OkHTTP
    implementation(libs.okhttp)
    implementation(libs.okhttp3.logging.interceptor)
    testImplementation(libs.mockwebserver)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore)
    implementation(libs.datastore.preferences)

    // TEST
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}