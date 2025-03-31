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

    // Estados para controlar el diálogo
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    var selectedVehicleId: String? = null

    init {
        fetchVehicles()
    }

    private var hasLoaded = false

    private fun fetchVehicles() {
        if (hasLoaded) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val userToken = tokenManager.token.first() ?: ""
                val response = apiService.getVehicleSinPreoperacional("Bearer $userToken")
                if (response.isSuccessful && response.body()!!.success) {
                    Log.i("datavehiculos", response.body().toString())
                    response.body()?.data?.let { dataList ->
                        _vehicles.value = dataList
                        hasLoaded = true
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Nuevas funciones para manejar el diálogo y acciones
    fun onVehicleSelected(vehicleId: String) {
        selectedVehicleId = vehicleId
        _showDialog.value = true
    }

    fun onDismissDialog() {
        _showDialog.value = false
    }

    fun onPerformPreoperacional() {
        _showDialog.value = false
        selectedVehicleId?.let { /* Aquí manejas la navegación al formulario */ }
    }

//    fun onSendEmptyPreoperacional() {
////        viewModelScope.launch(Dispatchers.IO) {
////            try {
////                selectedVehicleId?.let { vehicleId ->
////                    val userToken = tokenManager.token.first() ?: ""
////                    val response = apiService.sendEmptyPreoperacional(
////                        "Bearer $userToken",
////                        vehicleId
////                    )
////                    if (response.isSuccessful) {
////                        Log.i("Preoperacional", "Preoperacional vacío enviado para $vehicleId")
////                        // Recargar la lista
////                        hasLoaded = false
////                        fetchVehicles()
////                    }
////                }
////            } catch (e: Exception) {
////                Log.e("Preoperacional", "Error al enviar preoperacional vacío", e)
////            } finally {
////                _showDialog.value = false
////            }
////        }
////    }
}