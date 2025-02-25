package com.example.pesv_movil.login.data.network

import com.example.pesv_movil.login.data.LoginClient
import com.example.pesv_movil.login.data.network.response.LoginRequest
import com.example.pesv_movil.login.data.network.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class LoginService @Inject constructor(private val loginClient: LoginClient) {
    suspend fun doLogin(loginRequest: LoginRequest): Response<LoginResponse> {
        return withContext(Dispatchers.IO) {
            loginClient.login(loginRequest).execute()
        }
    }

}