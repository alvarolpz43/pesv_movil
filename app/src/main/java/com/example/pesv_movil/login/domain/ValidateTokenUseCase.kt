package com.example.pesv_movil.login.domain

import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.flow.first

class ValidateTokenUseCase(private val tokenManager: TokenManager) {
    suspend fun isTokenValid(): Boolean {
        val token = tokenManager.token.first()
        return !token.isNullOrBlank()
    }
}