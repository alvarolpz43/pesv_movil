package com.example.pesv_movil.data

import com.example.pesv_movil.Garaje.data.MyResponseVehiculo
import com.example.pesv_movil.Garaje.data.VehiculeRequest
import com.example.pesv_movil.components.MyResponseSelects
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("pesv/admin/user/{id}/vehiculos")
    fun getMyVehiculos(
        @Header("Authorization") token: String,
        @Path("id") id: String?
    ): Call<MyResponseVehiculo>

    @POST("pesv/user/vehiculos")
    fun registerVehicle(
        @Header("Authorization") token: String,
        @Body body: VehiculeRequest
    ): Response<Unit>

    @GET("pesv/vehiculos")
    fun getSelectData(
        @Header("Authorization") token: String
    ): Call<MyResponseSelects>
}