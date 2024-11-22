import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.gms.google-services")

}
val koin_version = "3.5.0"

val retrofitVersion = "2.9.0"
val okhttpVersion = "4.9.0"
val glideVersion = "4.11.0"



android {
    namespace = "com.example.appaei"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appaei"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    buildFeatures {
        dataBinding = true  // Habilita Data Binding
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:21.1.0") // Usa la última versión
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation("com.google.firebase:firebase-firestore-ktx:24.6.0")
    implementation("com.google.firebase:firebase-auth-ktx:21.6.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest);

// Koin core
    implementation("io.insert-koin:koin-core:$koin_version")
// Koin Androids
    implementation("io.insert-koin:koin-android:$koin_version")
// Koin para Jetpack Compose (si no está disponible la 3.5.0, prueba con 3.4.x)
    implementation("io.insert-koin:koin-androidx-compose:3.4.5")

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    implementation("com.airbnb.android:lottie:3.4.0")
    implementation("io.insert-koin:koin-core:3.x.x") // Replace 3.x.x with the latest version
    implementation("io.insert-koin:koin-android:3.x.x") // For Android projects
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")  // Dependencia para ConstraintLayout
    implementation ("androidx.cardview:cardview:1.0.")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.appcompat:appcompat:1.4.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0") // Si usas ViewModel
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0") // Si usas LiveData
}
