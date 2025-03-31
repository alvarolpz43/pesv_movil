package com.example.pesv_movil.preoperacional.ListaVehiculos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreoperacionalViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    private val _vehicles = MutableStateFlow<List<DataVehicleSinPre>>(emptyList())
    val vehicles: StateFlow<List<DataVehicleSinPre>> = _vehicles

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchVehicles()
    }

    private fun fetchVehicles() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val userToken = tokenManager.token.first() ?: ""
                val response = apiService.getVehicleSinPreoperacional("Bearer $userToken")
                if (response.isSuccessful && response.body()!!.success) {
                    response.body()?.data?.let { dataList ->
                        _vehicles.value = dataList
                        Log.i("Vehiculos", dataList.toString())
                    }
                } else {
                    Log.e("Error", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Error", "Ocurrió un error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}