package com.example.pesv_movil.Splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    init {
        viewModelScope.launch {
            delay(2000) // Tiempo mínimo de visualización del splash
            _state.value = if (tokenManager.isTokenExpiredBlocking()) {
                SplashState.NavigateToLogin
            } else {
                SplashState.NavigateToHome
            }
        }
    }
}



