package com.example.pesv_movil.Garaje.repositories

import android.util.Log
import com.example.pesv_movil.Garaje.dto.vehicleDtos.ArrResponseVehicleDto
import com.example.pesv_movil.Garaje.dto.DocsDataDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.InfoVehicleDto
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class GarajeRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    fun getMyVehicles(): Flow<List<InfoVehicleDto>> = flow {
        try {
            val token = tokenManager.token.first() ?: ""
            if (token != null) {
                val authHeader = "Bearer $token"
                val response = apiService.getMyVehicles(authHeader)

                if (response.success) {
                    emit(response.data)
                    Log.d("GarajeRepository", "getMyVehicles OK: ${response.data.size} items")
                } else {
                    emit(emptyList())
                    Log.w("GarajeRepository", "getMyVehicles -> success=false")
                }
            } else {
                emit(emptyList())
                Log.e("GarajeRepository", "getMyVehicles -> Token no disponible")
            }
        } catch (e: Exception) {
            Log.e("GarajeRepository", "getMyVehicles ERROR: ${e.message}", e)
            throw Exception("Error obteniendo vehículos", e)
        }
    }

    fun changeStateVehicle(idVehiculo: String): Flow<Unit> = flow {
        try {
            val token = tokenManager.token.first() ?: ""
            val authHeader = "Bearer $token"
            val response = apiService.updateVehicleState(authHeader, idVehiculo)

            if (response.isSuccessful) {
                emit(Unit)
                Log.d("GarajeRepository", "changeStateVehicle OK -> $idVehiculo")
            } else {
                val code = response.code()
                val body = response.errorBody()?.string()
                Log.e("GarajeRepository", "changeStateVehicle FAIL: $code - $body")
                throw Exception("Error al cambiar el estado del vehículo: $code")
            }
        } catch (e: Exception) {
            Log.e("GarajeRepository", "changeStateVehicle ERROR: ${e.message}", e)
            throw Exception("Error cambiando estado del vehículo", e)
        }
    }

    fun submitDocumentVehicle(
        documento: MultipartBody.Part?,
        tipoDocumentoId: RequestBody,
        idVehiculo: RequestBody,
        fechaExpiracion: RequestBody,
        numeroDocumento: RequestBody
    ): Flow<Unit> = flow {
        try {
            val token = tokenManager.token.first() ?: ""
            val authHeader = "Bearer $token"
            val response = apiService.submitDocumentVehicle(
                documento,
                tipoDocumentoId,
                idVehiculo,
                fechaExpiracion,
                numeroDocumento
            )

            if (response.isSuccessful) {
                emit(Unit)
                Log.d("GarajeRepository", "submitDocumentVehicle OK")
            } else {
                val code = response.code()
                val msg = response.errorBody()?.string() ?: "Error desconocido"
                Log.e("GarajeRepository", "submitDocumentVehicle FAIL: $code - $msg")
                throw Exception("Error al subir documento: $code - $msg")
            }
        } catch (e: Exception) {
            Log.e("GarajeRepository", "submitDocumentVehicle ERROR: ${e.message}", e)
            throw Exception("Error subiendo documento del vehículo", e)
        }
    }

    fun getDocumentsVehicle(id: String): Flow<List<DocsDataDto>> = flow {
        try {
            val token = tokenManager.token.first() ?: ""
            val authHeader = "Bearer $token"
            val response = apiService.getMyDocumentVehicle(authHeader, id)

            if (response.success) {
                emit(response.data)
                Log.d("GarajeRepository", "getDocumentsVehicle OK: ${response.data.size} docs")
            } else {
                emit(emptyList())
                Log.w("GarajeRepository", "getDocumentsVehicle -> success=false")
            }
        } catch (e: Exception) {
            Log.e("GarajeRepository", "getDocumentsVehicle ERROR: ${e.message}", e)
            throw Exception("Error obteniendo documentos del vehículo", e)
        }
    }

    fun getTipoDctoVehicleAndUser(): Flow<ArrResponseVehicleDto> = flow {
        try {
            val token = tokenManager.token.first() ?: ""
            val auth = "Bearer $token"

            val resp = apiService.getSelectionTipoDocumento(auth)
            if (resp.success) {
                emit(resp.data) // contiene tipoDocVehiculo y tipoDocUsuario
                Log.d(
                    "GarajeRepository",
                    "getTipoDctoVehicleAndUser OK -> veh: ${resp.data.tipoDocVehiculo.size}, usr: ${resp.data.tipoDocUsuario.size}"
                )
            } else {
                Log.e("GarajeRepository", "getTipoDctoVehicleAndUser -> success=false")
                throw Exception("No fue posible obtener los tipos de documento (success=false)")
            }
        } catch (e: Exception) {
            Log.e("GarajeRepository", "getTipoDctoVehicleAndUser ERROR: ${e.message}", e)
            throw Exception("Error obteniendo tipos de documento", e)
        }
    }
}
