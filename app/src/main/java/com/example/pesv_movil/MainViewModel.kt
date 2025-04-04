package com.example.pesv_movil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    fun checkAuthentication(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Simular carga inicial necesaria
            delay(200) // Mínimo tiempo para mostrar el branding

            // Verificar autenticación real
            val isAuthenticated = !tokenManager.isTokenExpiredBlocking()

            isLoading = false
            onComplete(isAuthenticated)
        }
    }
}