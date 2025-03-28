package com.example.pesv_movil.preoperacional

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pesv_movil.Garaje.data.VehiculeRequest
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre
import com.example.pesv_movil.preoperacional.data.ResponseVehicleSinPre
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreoperacionalViewModel(
    private val navController: NavController,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    suspend fun getCall(): List<DataVehicleSinPre>? {
        val userToken = tokenManager.token.first() ?: ""

        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getVehicleSinPreoperacional("Bearer $userToken")

                if (response.isSuccessful) {
                    response.body()?.data?.also { dataList ->
                        Log.i("Vehiculos", dataList.toString())
                    }
                } else {
                    Log.e("Error", "Error en la respuesta: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("Error", "Ocurrió un error: ${e.message}")
                null
            }
        }
    }

    fun goToForm(id_vehiculo: String) {
        Log.i("id_vehicle", id_vehiculo)
        navController.navigate("form_preoperacional/${id_vehiculo}")

    }


}

