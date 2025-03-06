package com.example.pesv_movil.desplazamientos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class DesplazamientosViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {



    private val placesClient: PlacesClient = Places.createClient(context); //Creamos el placesClient
    private val _autocompletePredictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val autocompletePredictions: StateFlow<List<AutocompletePrediction>> = _autocompletePredictions

    private val _autocompleteLoading = MutableStateFlow(false)
    val autocompleteLoading: StateFlow<Boolean> = _autocompleteLoading

    private val _autocompleteError = MutableStateFlow<String?>(null)
    val autocompleteError: StateFlow<String?> = _autocompleteError


    fun obtenerPredicciones(query: String) {
        if (query.isBlank()) {
            _autocompletePredictions.value = emptyList()
            return
        }

        viewModelScope.launch {
            _autocompleteLoading.value = true
            _autocompleteError.value = null
            try {
                val token = AutocompleteSessionToken.newInstance()
                val request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setQuery(query)
                    .setTypeFilter(TypeFilter.ADDRESS) // Filtra por direcciones (puedes ajustar esto)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        _autocompletePredictions.value = response.autocompletePredictions
                        _autocompleteLoading.value = false
                    }
                    .addOnFailureListener { exception ->
                        _autocompleteError.value = exception.message ?: "Error al obtener predicciones"
                        _autocompleteLoading.value = false
                    }
            } catch (e: Exception) {
                _autocompleteError.value = e.message ?: "Error desconocido"
                _autocompleteLoading.value = false
            }
        }
    }


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

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // -------------------------
    // 🔹 Métodos para actualizar estados
    // -------------------------
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



