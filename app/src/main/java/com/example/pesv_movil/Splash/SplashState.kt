package com.example.pesv_movil.Splash

sealed class SplashState {
    object Loading : SplashState()
    object NavigateToHome : SplashState()
    object NavigateToLogin : SplashState()
}