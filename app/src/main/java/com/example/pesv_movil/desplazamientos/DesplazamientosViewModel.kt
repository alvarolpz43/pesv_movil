package com.example.pesv_movil.desplazamientos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.pesv_movil.PesvNavGraph
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.desplazamientos.model.OpcionVehiculo
import com.example.pesv_movil.desplazamientos.network.ApiDesplazamientos
import com.example.pesv_movil.utils.TokenManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class DesplazamientosViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenManager: TokenManager

) : ViewModel() {
    private val apiService: ApiDesplazamientos =
        RetrofitHelper.getRetrofit().create(ApiDesplazamientos::class.java)

    // Estados para la ubicación de origen y destino
    private val _origenSeleccionado = MutableStateFlow(DEFAULT_LOCATION)
    val origenSeleccionado: StateFlow<LatLng> = _origenSeleccionado

    private val _destinoSeleccionado = MutableStateFlow(DEFAULT_LOCATION)
    val destinoSeleccionado: StateFlow<LatLng> = _destinoSeleccionado

    // Estados para el modal y la ubicación del usuario
    var showModal = mutableStateOf(false)
    var userLocation = mutableStateOf<LatLng?>(null)
    var locationLoading = mutableStateOf(false)
    var locationError = mutableStateOf<String?>(null)


    private val _opciones = MutableStateFlow<List<OpcionVehiculo>>(emptyList())
    val opciones: StateFlow<List<OpcionVehiculo>> = _opciones


    var vehiculoSeleccionado by mutableStateOf("")
        private set

    var p_inicio by mutableStateOf<String>("")
    var p_final by mutableStateOf<String>("")
    var numeroText by mutableStateOf("")

    var p_inicioError by mutableStateOf<String?>(null)
        private set

    var p_FinalError by mutableStateOf<String?>(null)
        private set

    var vehiculoSeleccionadoError by mutableStateOf<String?>(null)
        private set

    val numero: Int?
        get() = numeroText.toIntOrNull()

    fun onVehiculoSeleccionado(value: String) {
        vehiculoSeleccionado = value

        if (value.isNotBlank()) {
            vehiculoSeleccionadoError = null
        }
    }

    fun onChangePuntoInicio(value: String) {
        p_inicio = value
        if (p_inicioError != null && value.isNotBlank()) {
            p_inicioError = null
        }
    }

    init {

        getVehiculos()
    }

    private fun getVehiculos() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = tokenManager.token.first() ?: ""

            val response = apiService.getUserVehiculos("Bearer $token");

            if (response.isSuccessful && response.body()!!.success) {
                Log.e("rojo", response.body()?.data.toString())


                val vehiculos = response.body()?.data ?: emptyList()

                _opciones.value = vehiculos.map {
                    OpcionVehiculo(
                        id = it._id,
                        displayText = "${it.marca} - ${it.placa}"
                    )
                }


            }
        }


    }


    fun onChangePuntoFinal(value: String) {
        p_final = value

        if (p_final != null && value.isNotBlank()) {
            p_FinalError = null
        }
    }


    fun enviarFormulario(navHostController: NavHostController) {
        var valido = true

        if (p_inicio.isBlank()) {
            p_inicioError = "El Punto de Inicio es requerido"
            valido = false

        }
        if (p_final.isBlank()) {
            p_FinalError = "El Punto de Final es requerido"
            valido = false

        }
        if (vehiculoSeleccionado == "") {
            vehiculoSeleccionadoError = "Selecciona un Vehiculo"
            valido = false
        }

        if (valido) {
            Toast.makeText(
                this.context,
                "Formulario Enviado $vehiculoSeleccionado",
                Toast.LENGTH_SHORT
            ).show()

            navHostController.navigate(PesvScreens.DESPLAZAMIENTOS_SCREEN)


        }
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // ------------------
// 🔹 Métodos para actualizar estados
// ------------------
    fun setOrigen(nuevoOrigen: LatLng) {
        _origenSeleccionado.value = nuevoOrigen
    }

    fun setDestino(nuevoDestino: LatLng) {
        _destinoSeleccionado.value = nuevoDestino
    }

    // -------------------------
// 🔹 Métodos para manejar la ubicación
// -------------------------
    fun getCurrentLocation() {
        if (!verificarPermisosUbicacion()) return
        if (!verificarUbicacionHabilitada()) return

        locationLoading.value = true
        viewModelScope.launch {
            try {
                userLocation.value = obtenerUbicacionSegura()
            } catch (e: Exception) {
                locationError.value = e.message ?: "Error desconocido"
            } finally {
                locationLoading.value = false
            }
        }
    }

    private fun verificarPermisosUbicacion(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            locationError.value = "Permiso de ubicación denegado."
            false
        }
    }

    private fun verificarUbicacionHabilitada(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val habilitada = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

        if (!habilitada) {
            locationError.value = "La ubicación está desactivada. Actívala en la configuración."
        }
        return habilitada
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    private suspend fun obtenerUbicacionSegura(): LatLng? =
        suspendCancellableCoroutine { continuation ->
            if (!verificarPermisosUbicacion()) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    continuation.resume(LatLng(location.latitude, location.longitude))
                } else {
                    locationError.value = "No se pudo obtener la ubicación"
                    continuation.resume(null)
                }
            }.addOnFailureListener { e ->
                locationError.value = "Error al obtener ubicación: ${e.message}"
                continuation.resume(null)
            }
        }

    companion object {
        private val DEFAULT_LOCATION = LatLng(4.60971, -74.08175) // Ubicación por defecto
    }
}



