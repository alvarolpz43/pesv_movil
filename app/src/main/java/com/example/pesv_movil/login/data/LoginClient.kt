package com.example.pesv_movil.login.data

import com.example.pesv_movil.login.data.network.response.LoginRequest
import com.example.pesv_movil.login.data.network.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginClient {
    @POST("auth/users/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}