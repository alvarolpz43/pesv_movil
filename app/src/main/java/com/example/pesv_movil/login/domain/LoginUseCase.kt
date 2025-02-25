package com.example.pesv_movil.login.domain

import com.example.pesv_movil.login.data.LoginRepository
import com.example.pesv_movil.login.data.network.response.LoginRequest
import com.example.pesv_movil.login.data.network.response.LoginResponse
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: LoginRepository) {
    suspend operator fun invoke(loqinRequest: LoginRequest): LoginResponse {
        return repository.doLogin(loqinRequest)

    }
}