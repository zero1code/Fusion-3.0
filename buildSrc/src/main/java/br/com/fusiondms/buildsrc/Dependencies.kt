package br.com.fusiondms.buildsrc

import org.gradle.api.JavaVersion

object MobilecastConfig {
    val javaVersion = JavaVersion.VERSION_1_8
    const val minSdk = 26
    const val compileSdk = 33
    const val targetSdk = 33
    const val applicationId = "br.com.fusiondms.mobilecast"
    const val versionCode = 1
    const val versionName = "1.0.0"
}

object JornadaTrabalhoConfig {
    val javaVersion = JavaVersion.VERSION_1_8
    const val minSdk = 26
    const val compileSdk = 33
    const val targetSdk = 33
    const val applicationId = "br.com.fusiondms.jornadatrabalho"
    const val versionCode = 1
    const val versionName = "1.0.0"
}

object Versions {
    const val ktx = "1.9.0"
    const val support = "1.3.1"
    const val material = "1.4.0"
    const val constraint_layout = "2.1.1"
    const val fragment_androidx = "1.3.6"
    const val navigation_ui = "2.5.2"
    const val navigation_ui_ktx = "2.5.2"
    const val navigation_androidx = "2.5.2"
    const val navigation_runtime = "2.5.2"
    const val navigation_runtime_ktx = "2.5.2"
    const val navigation_fragment = "2.5.2"
    const val coroutines_core = "1.6.4"
    const val coroutines_android = "1.3.9"
    const val dagger_hilt = "2.43.2"
    const val kapt_dagger_hilt_compiler = "2.43.2"
    const val kapt_hilt_compiler = "1.0.0"
    const val splash_screen = "1.0.0"
    const val sliding_pane_layout = "1.2.0"
    const val coil = "2.2.1"
    const val play_services_maps = "18.1.0"
    const val play_services_location = "19.0.0"
    const val room_runtime = "2.4.3"
    const val room_ktx = "2.4.3"
    const val room_kapt_compiler = "2.4.3"
    const val gson = "2.9.1"
    const val retrofit = "2.9.0"
    const val retrofit_converter = "2.9.0"
    const val okhttp = "4.10.0"
    const val datastore = "1.0.0"
}

object Deps {
    const val kotlin_ktx = "androidx.core:core-ktx:${Versions.ktx}"
    const val support_app_compat = "androidx.appcompat:appcompat:${Versions.support}"
    const val support_material = "com.google.android.material:material:${Versions.material}"
    const val support_constraint_layout = "androidx.constraintlayout:constraintlayout:${Versions.constraint_layout}"
    const val fragment_androidx = "androidx.fragment:fragment-ktx:${Versions.fragment_androidx}"
    const val navigation_fragment = "androidx.navigation:navigation-fragment:${Versions.navigation_fragment}"
    const val navigation_ui_ktx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation_ui_ktx}"
    const val navigation_ui = "androidx.navigation:navigation-ui:${Versions.navigation_ui}"
    const val navigation_runtime = "androidx.navigation:navigation-runtime:${Versions.navigation_runtime}"
    const val navigation_runtime_ktx = "androidx.navigation:navigation-runtime-ktx:${Versions.navigation_runtime_ktx}"
    const val navigation_androidx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation_androidx}"
    const val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines_core}"
    const val coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines_android}"
    const val dagger_hilt = "com.google.dagger:hilt-android:${Versions.dagger_hilt}"
    const val kapt_dagger_hilt_compiler = "com.google.dagger:hilt-compiler:${Versions.kapt_dagger_hilt_compiler}"
    const val kapt_hilt_compiler = "androidx.hilt:hilt-compiler:${Versions.kapt_hilt_compiler}"
    const val splash_screen = "androidx.core:core-splashscreen:${Versions.splash_screen}"
    const val sliding_pane_layout = "androidx.slidingpanelayout:slidingpanelayout:${Versions.sliding_pane_layout}"
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val play_services_maps = "com.google.android.gms:play-services-maps:${Versions.play_services_maps}"
    const val play_services_location = "com.google.android.gms:play-services-location:${Versions.play_services_location}"
    const val room_runtime = "androidx.room:room-runtime:${Versions.room_runtime}"
    const val room_ktx = "androidx.room:room-ktx:${Versions.room_ktx}"
    const val room_kapt_compiler = "androidx.room:room-compiler:${Versions.room_kapt_compiler}"
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofit_converter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit_converter}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val datastore = "androidx.datastore:datastore-preferences:${Versions.datastore}"
}

object LocalLibs {
    const val app = ":app"
}