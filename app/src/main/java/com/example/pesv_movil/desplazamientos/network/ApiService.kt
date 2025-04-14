package com.example.pesv_movil.desplazamientos.network


import com.example.pesv_movil.Garaje.data.MyResponseVehiculo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiDesplazamientos {

    @GET("pesv/user/vehiculos")
    suspend fun getUserVehiculos(
        @Header("Authorization") token: String,
    ): Response<MyResponseVehiculo>

}