package com.example.pesv_movil.login.data

import com.example.pesv_movil.login.data.network.LoginService
import com.example.pesv_movil.login.data.network.response.LoginRequest
import com.example.pesv_movil.login.data.network.response.LoginResponse
import javax.inject.Inject

class LoginRepository @Inject constructor(private val api: LoginService) {
    suspend fun doLogin(loginRequest: LoginRequest): LoginResponse {
        val response = api.doLogin(loginRequest)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacia")
        } else {
            throw Exception("Error de inicio de sesion: ${response.code()}")
        }

    }
}