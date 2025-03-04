package com.example.pesv_movil.data.repositories

import android.util.Log
import com.example.pesv_movil.Garaje.data.MyResponseDocsVehicle
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.flow.first

class DocsVehiclesRepository(private val apiService: ApiService, private val tokenManager: TokenManager) {

    suspend fun fecthDocsData(id:String): MyResponseDocsVehicle?{

        val tokenValue = tokenManager.token.first() ?: ""
        return try {
            val call = apiService.getMyDocumentsVehicle("Bearer $tokenValue", id)
            val response = call.execute()
            if(response.isSuccessful){
                response.body()
            } else {
                null
            }

        }catch (e: Exception){
            Log.e("DocsDatasRepository", "Error fetching selects: ${e.message}")
            null
        }
    }
}