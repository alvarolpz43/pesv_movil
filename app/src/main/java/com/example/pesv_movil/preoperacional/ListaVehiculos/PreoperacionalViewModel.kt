package com.example.pesv_movil.preoperacional.ListaVehiculos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre
import com.example.pesv_movil.preoperacional.data.RequestBodyFormNoAplica
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PreoperacionalViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    private val _vehicles = MutableStateFlow<List<DataVehicleSinPre>>(emptyList())
    val vehicles: StateFlow<List<DataVehicleSinPre>> = _vehicles

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _showNoAplicaSuccess = MutableStateFlow(false)
    val showNoAplicaSuccess: StateFlow<Boolean> = _showNoAplicaSuccess.asStateFlow()

    private val _showErrorEvent = MutableLiveData<String>()
    val showErrorEvent: LiveData<String> = _showErrorEvent

    var selectedVehicleId: String? = null
        private set

    private var hasLoaded = false

    init {
        fetchVehicles()
    }

    private fun fetchVehicles(forceReload: Boolean = false) {
        if (!forceReload && hasLoaded) return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val token = tokenManager.token.first() ?: ""
                val response = apiService.getVehicleSinPreoperacional("Bearer $token")

                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        _vehicles.value = it
                        hasLoaded = true
                    }
                    Log.i("PreoperacionalViewModel", "Vehículos cargados correctamente")
                } else {
                    _showErrorEvent.postValue("Error al cargar vehículos: ${response.message()}")
                }
            } catch (e: Exception) {
                _showErrorEvent.postValue("Excepción: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onVehicleSelected(vehicleId: String) {
        selectedVehicleId = vehicleId
        _showDialog.value = true
    }

    fun onDismissDialog() {
        _showDialog.value = false
    }

    fun onPerformPreoperacional() {
        _showDialog.value = false
    }

    fun sendPreoperacionalNoAplica(idVehiculo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = tokenManager.token.first()
                val response = apiService.registerFormNoAplica(
                    "Bearer $token",
                    RequestBodyFormNoAplica(idVehiculo)
                )
                if (response.isSuccessful) {
                    _showNoAplicaSuccess.value = true
                    hasLoaded = false // 🟡 Fuerza la recarga
                    fetchVehicles(forceReload = true)
                    Log.i("NoAplica", "Formulario registrado correctamente.")
                } else {
                    Log.e("NoAplicaError", response.errorBody()?.string() ?: "Error desconocido")
                }
            } catch (e: Exception) {
                Log.e("NoAplicaException", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetNoAplicaSuccess() {
        _showNoAplicaSuccess.value = false
    }
}
