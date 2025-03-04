package com.example.pesv_movil.data.repositories

import android.util.Log
import com.example.pesv_movil.components.MyResponseTipoDctoVehicle
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.flow.first

class TipoDocumentoRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun fecthTipoDocumento(): MyResponseTipoDctoVehicle? {
        val tokenValue = tokenManager.token.first() ?: ""

        return try {
            val call = apiService.getSelectTipoDocumento("Bearer $tokenValue")
            val response = call.execute()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }

        } catch (e: Exception) {
            Log.e("TipoDocRepository", "Error fetching selects: ${e.message}")
            null
        }

    }
}