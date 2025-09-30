package com.example.pesv_movil.data

import com.example.pesv_movil.Garaje.data.MyResponseDocsVehicle
import com.example.pesv_movil.Garaje.data.VehiculeRequest
import com.example.pesv_movil.Garaje.dto.selectsDtos.ResponseSelectsDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.ResponseDocsVehicleDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.ResponseTipoDctoVehicleDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.ResponseVehicleDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.VehicleRequestDto
import com.example.pesv_movil.Notificaciones.data.MyResponseNotifications
import com.example.pesv_movil.components.MyResponseSelects
import com.example.pesv_movil.components.MyResponseTipoDctoVehicle
import com.example.pesv_movil.preoperacional.data.RequestBodyForm
import com.example.pesv_movil.preoperacional.data.RequestBodyFormNoAplica
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
    suspend fun getMyVehicles(
        @Header("Authorization") token: String,
    ): ResponseVehicleDto


//    @GET("pesv/user/vehiculos")
//    suspend fun getMyVehiculos(
//        @Header("Authorization") token: String,
//    ): Response<MyResponseVehiculo>


    //Eliminar Despues
    @GET("pesv/vehiculos/documents/{id}")
    fun getMyDocumentsVehicle(
        @Header("Authorization") token: String,
        @Path("id") id: String?
    ): Call<MyResponseDocsVehicle>

    @GET("pesv/vehiculos/documents/{id}")
    suspend fun getMyDocumentVehicle(
        @Header("Authorization") token: String,
        @Path("id") id: String?
    ): ResponseDocsVehicleDto


    //Eliminar Despues
    @GET("pesv/vehiculos")
    fun getSelectData(
        @Header("Authorization") token: String
    ): Call<MyResponseSelects>

    @GET("pesv/vehiculos")
   suspend fun getSelectsData(): ResponseSelectsDto

    //Eliminar Despues
    @GET("pesv/documents/tipos/vehiculos")
    fun getSelectTipoDocumento(
        @Header("Authorization") token: String
    ): Call<MyResponseTipoDctoVehicle>


    @GET("pesv/documents/tipos/vehiculos")
   suspend fun getSelectionTipoDocumento(
        @Header("Authorization") token: String
    ): ResponseTipoDctoVehicleDto


    @GET("pesv/vehiculos/vehiculos-sin-preoperacional") //Fun duplicada para pruebas
    suspend fun getVehicleSinPreoperacional(
        @Header("Authorization") token: String
    ): Response<ResponseVehicleSinPre>


    @GET("pesv/formularios/vehiculo/{idVehiculo}")
    suspend fun getFormById(
        @Header("Authorization") token: String,
        @Path("idVehiculo") idVehiculo: String
    ): Response<ResponseFormById>


    @GET("pesv/notificaciones/user")
    fun getMyNotificaciones(
        @Header("Authorization") token: String,
    ): Call<MyResponseNotifications>


    //Eliminar Despues
    @POST("pesv/user/vehiculos")
    suspend fun registerVehicle(
        @Header("Authorization") token: String,
        @Body body: VehiculeRequest
    ): Response<Unit>

    @POST("pesv/user/vehiculos")
    suspend fun registerVehicles(
        @Header("Authorization") token: String,
        @Body body: VehicleRequestDto
    ): Response<Unit>

    @POST("/pesv/preoperacional")
    suspend fun registerForm(
        @Header("Authorization") token: String,
        @Body body: RequestBodyForm
    ): Response<Unit>

    @POST("/pesv/preoperacional/no-aplica")
    suspend fun registerFormNoAplica(
        @Header("Authorization") token: String,
        @Body body: RequestBodyFormNoAplica
    ): Response<Unit>


    //Eliminar Despues
    @Multipart
    @POST("pesv/documents/uploadVehiculeId")
    suspend fun uploadDocumentVehicle(
        @Part documento: MultipartBody.Part?,
        @Part("tipoDocumentoId") tipoDocumentoId: RequestBody,
        @Part("idVehiculo") idVehiculo: RequestBody,
        @Part("fechaExpiracion") fechaExpiracion: RequestBody,
        @Part("numeroDocumento") numeroDocumento: RequestBody,
    ): Response<ResponseBody>


    @Multipart
    @POST("pesv/documents/uploadVehiculeId")
    suspend fun submitDocumentVehicle(
        @Part documento: MultipartBody.Part?,
        @Part("tipoDocumentoId") tipoDocumentoId: RequestBody,
        @Part("idVehiculo") idVehiculo: RequestBody,
        @Part("fechaExpiracion") fechaExpiracion: RequestBody,
        @Part("numeroDocumento") numeroDocumento: RequestBody,
    ): Response<ResponseBody>


    //Eliminar despues
    @PUT("pesv/vehiculos/edit/estado-uso/{idVehiculo}")
    suspend fun updateVehicleStateUsing(
        @Header("Authorization") token: String,
        @Path("idVehiculo") idVehiculo: String
    ): Response<Unit>

    @PUT("pesv/vehiculos/edit/estado-uso/{idVehiculo}")
    suspend fun updateVehicleState(
        @Header("Authorization") token: String,
        @Path("idVehiculo") idVehiculo: String
    ): Response<Unit>

}