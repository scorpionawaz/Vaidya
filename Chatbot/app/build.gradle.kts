plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.chatbot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chatbot"
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
        sourceCompatibility = JavaVersion.VERSION_1_8    // Java 8 language features :contentReference[oaicite:2]{index=2}
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true           // Turn on core-library desugar
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.tasks)
    implementation(libs.vision.common)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    // optional: for JSON building/parsing
    implementation("org.json:json:20230227")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")

    // Barcode Scanning (Bundled मॉडल)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    // Face Detection (Bundled मॉडल)
    implementation("com.google.mlkit:text-recognition:16.0.0")
    // Image Labeling (Bundled मॉडल)
    implementation("com.google.mlkit:image-labeling:17.0.9")

    // Object Detection & Tracking (Bundled मॉडल)
    implementation("com.google.mlkit:object-detection:17.0.2")

    // Language Identification (Bundled मॉडल)
    implementation("com.google.mlkit:language-id:17.0.6")

}