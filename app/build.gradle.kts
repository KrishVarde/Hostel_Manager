plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.hostel.manager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hostel.manager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
}

configurations.all {
    resolutionStrategy {
        force("com.google.errorprone:error_prone_annotations:2.18.0")
        force("androidx.core:core:1.13.1")
    }
}

dependencies {
    implementation("androidx.core:core:1.13.1")
    implementation("com.google.errorprone:error_prone_annotations:2.18.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
}
