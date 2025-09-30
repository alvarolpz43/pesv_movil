package com.example.pesv_movil.Garaje.repositories

import android.util.Log
import com.example.pesv_movil.Garaje.dto.selectsDtos.ResponseSelectsDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.VehicleRequestDto
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FormVehicleRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    fun createVehicle(body: VehicleRequestDto): Flow<Unit> = flow {
        try {
            val token = tokenManager.token.first() ?: ""
            val authHeader = "Bearer $token"

            val response = apiService.registerVehicles(authHeader, body)
            if (response.isSuccessful) {
                emit(Unit)
                Log.d("FormVehicleRepository", "createVehicle OK")
            } else {
                val code = response.code()
                val msg = response.errorBody()?.string() ?: "Error desconocido"
                Log.e("FormVehicleRepository", "createVehicle FAIL: $code - $msg")
                throw Exception("Error al crear vehículo: $code - $msg")
            }

        } catch (e: Exception) {
            Log.e("FormVehicleRepository", "createVehicle ERROR: ${e.message}", e)
            throw Exception("Error creando vehículo", e)

        }
    }

    fun getSelectsData(): Flow<ResponseSelectsDto> = flow {
        try {
            val token = tokenManager.token.first() ?: ""
            val authHeader = "Bearer $token"
            val response = apiService.getSelectsData()
            if (response.success) {
                Log.d("FormVehicleRepository", response.toString())
                emit(response)
                Log.d("FormVehicleRepository", "getSelectsData OK")
            } else {
                Log.e("FormVehicleRepository", "getSelectsData -> success=false")
                throw Exception("Error al obtener los selects")
            }

        } catch (e: Exception) {
            Log.e("FormVehicleRepository", "getSelectsData ERROR: ${e.message}", e)
            throw Exception("Error obteniendo selects", e)
        }
    }
}