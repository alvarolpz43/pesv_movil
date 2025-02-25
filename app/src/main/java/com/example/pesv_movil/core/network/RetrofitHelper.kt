package com.example.pesv_movil.core.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    fun getRetrofit():  Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://backend-pesv.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}