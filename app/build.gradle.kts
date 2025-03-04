plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.pesv_movil"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pesv_movil"
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
// 🔹 Hilt para inyección de dependencias
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")

    // 🔹 Compatibilidad con SplashScreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // 🔹 Jetpack Compose (VERSIONES ESPECÍFICAS)
    implementation("androidx.compose.ui:ui:1.6.2") // Última versión estable
    implementation("androidx.compose.ui:ui-graphics:1.6.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.2")

    // 🔹 Material 3 (Asegurar compatibilidad con Compose)
    implementation("androidx.compose.material3:material3:1.2.1")

    // 🔹 Material Icons Extendidos
    implementation("androidx.compose.material:material-icons-extended:1.6.2")

    // 🔹 Animaciones para evitar conflictos con `KeyframesSpec`
    implementation("androidx.compose.animation:animation:1.6.2")

    // 🔹 Navegación en Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // 🔹 Lifecycle para Jetpack Compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // 🔹 Decodificación de JWT
    implementation("com.auth0.android:jwtdecode:2.0.0")

    // 🔹 Retrofit para llamadas a API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")

    // 🔹 Glide para manejo de imágenes
    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")

    // 🔹 DataStore para almacenamiento local
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // 🔹 Accompanist para SwipeRefresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

    implementation("androidx.compose.runtime:runtime-livedata:1.6.0")



    // Dependencias del catálogo (libs.versions.toml) - Elimina duplicados
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00")) // ⬅️ Asegura que usa la misma versión
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
