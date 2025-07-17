plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt")
    alias(libs.plugins.hilt.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.rururi.closedtestmate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rururi.closedtestmate"
        minSdk = 24
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
        compose = true
    }
}

dependencies {
    //Coil
    implementation(libs.coil.compose)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)

    //Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    //Hiltテスト用
    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)

    //ナビゲーション
    implementation(libs.androidx.navigation.compose)            //navigation
    androidTestImplementation(libs.androidx.navigation.testing)   //navigationTest
    //M3関係
    implementation(libs.androidx.material.icons.extended)   //アイコンを使用する場合
    debugImplementation(libs.ui.tooling)    //プレビュー用

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
}

kapt {
    correctErrorTypes = true
}