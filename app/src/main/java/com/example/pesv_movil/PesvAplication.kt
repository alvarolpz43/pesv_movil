package com.example.pesv_movil

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PesvAplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //Inicio PLacesSisaNada
        Places.initialize(applicationContext, "AIzaSyA-4ie9c0RABsH9UVO79peIAG5FgJ0PES8");




    }
}