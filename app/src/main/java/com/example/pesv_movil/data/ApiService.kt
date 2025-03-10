package com.example.pesv_movil.data

import com.example.pesv_movil.Garaje.data.MyResponseDocsVehicle
import com.example.pesv_movil.Garaje.data.MyResponseVehiculo
import com.example.pesv_movil.Garaje.data.VehiculeRequest
import com.example.pesv_movil.Notificaciones.data.MyResponseNotifications
import com.example.pesv_movil.components.MyResponseSelects
import com.example.pesv_movil.components.MyResponseTipoDctoVehicle
import com.example.pesv_movil.preoperacional.data.RequestBodyForm
import com.example.pesv_movil.preoperacional.data.ResponseFormById
import com.example.pesv_movil.preoperacional.data.ResponseVehicleSinPre
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("pesv/user/vehiculos")
    fun getMyVehiculos(
        @Header("Authorization") token: String,
    ): Call<MyResponseVehiculo>

//    @GET("pesv/user/vehiculos")
//    suspend fun getMyVehiculos(
//        @Header("Authorization") token: String,
//    ): Response<MyResponseVehiculo>


    @GET("pesv/vehiculos/documents/{id}")
    fun getMyDocumentsVehicle(
        @Header("Authorization") token: String,
        @Path("id") id: String?
    ): Call<MyResponseDocsVehicle>


    @GET("pesv/vehiculos")
    fun getSelectData(
        @Header("Authorization") token: String
    ): Call<MyResponseSelects>

    @GET("pesv/documents/tipos/vehiculos")
    fun getSelectTipoDocumento(
        @Header("Authorization") token: String
    ): Call<MyResponseTipoDctoVehicle>

    @GET("pesv/vehiculos/vehiculos-sin-preoperacional")
    fun getVehicleSinPre(
        @Header("Authorization") token: String
    ): Call<ResponseVehicleSinPre>

    @GET("pesv/formularios/vehiculo/{idVehiculo}")
    fun getFormById(
        @Header("Authorization") token: String,
        @Path("idVehiculo") idVehiculo: String
    ): Call<ResponseFormById>


    @GET("pesv/notificaciones/user")
    fun getMyNotificaciones(
        @Header("Authorization") token: String,
    ): Call<MyResponseNotifications>


    @POST("pesv/user/vehiculos")
    suspend fun registerVehicle(
        @Header("Authorization") token: String,
        @Body body: VehiculeRequest
    ): Response<Unit>

    @POST("/pesv/preoperacional")
    suspend fun registerForm(
        @Header("Authorization") token: String,
        @Body body: RequestBodyForm
    ): Response<Unit>

    @Multipart
    @POST("pesv/documents/uploadVehiculeId")
    suspend fun uploadDocumentVehicle(
        @Part documento: MultipartBody.Part?,
        @Part("tipoDocumentoId") tipoDocumentoId: RequestBody,
        @Part("idVehiculo") idVehiculo: RequestBody,
        @Part("fechaExpiracion") fechaExpiracion: RequestBody,
        @Part("numeroDocumento") numeroDocumento: RequestBody,
    ): Response<ResponseBody>

    @PUT("pesv/vehiculos/edit/estado-uso/{idVehiculo}")
    suspend fun updateVehicleStateUsing(
        @Header("Authorization") token: String,
        @Path("idVehiculo") idVehiculo: String
    ): Response<Unit>
}