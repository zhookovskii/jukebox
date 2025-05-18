plugins {
    alias(libs.plugins.dependencyAnalysis)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.daggerHiltAndroid)
}

android {
    namespace = "com.zhukovskii.jukebox_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zhukovskii.jukebox_app"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)


    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    implementation(project(":song-list"))
    implementation(project(":util"))
    implementation(project(":design-core"))
}