package com.example.pesv_movil.Garaje.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.Garaje.data.FormDataDocumentation
import com.example.pesv_movil.Garaje.dto.selectsDtos.ClasesListDto
import com.example.pesv_movil.Garaje.dto.selectsDtos.ResponseSelectsDto
import com.example.pesv_movil.Garaje.dto.selectsDtos.ServiciosListDto
import com.example.pesv_movil.Garaje.dto.selectsDtos.TiposListDto
import com.example.pesv_movil.Garaje.dto.selectsDtos.ZonasListDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.DataTipoDctoVehicleDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.VehicleRequestDto
import com.example.pesv_movil.Garaje.repositories.FormVehicleRepository
import com.example.pesv_movil.Garaje.usecases.FormVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class FormViewModel @Inject constructor(
    private val useCase: FormVehicleUseCase,
    private val repo: FormVehicleRepository
) : ViewModel() {


    private val _zonas = MutableStateFlow<List<ZonasListDto>>(emptyList())
    val zonas: StateFlow<List<ZonasListDto>> = _zonas

    private val _clases = MutableStateFlow<List<ClasesListDto>>(emptyList())
    val clases: StateFlow<List<ClasesListDto>> = _clases

    private val _tipos = MutableStateFlow<List<TiposListDto>>(emptyList())
    val tipos: StateFlow<List<TiposListDto>> = _tipos

    private val _servicios = MutableStateFlow<List<ServiciosListDto>>(emptyList())
    val servicios: StateFlow<List<ServiciosListDto>> = _servicios

    private val _loadingSelects = MutableStateFlow(false)
    val loadingSelects: StateFlow<Boolean> = _loadingSelects

    private val _creatingVehicle = MutableStateFlow(false)
    val creatingVehicle: StateFlow<Boolean> = _creatingVehicle

    private val _errorcreatingVehicle = MutableStateFlow<String?>(null)
    val errorcreatingVehicle: StateFlow<String?> = _errorcreatingVehicle

    private val _errorSelects = MutableStateFlow<String?>(null)
    val errorSelects: StateFlow<String?> = _errorSelects

    fun loadSelectsData() {
        viewModelScope.launch {
            repo.getSelectsData()
                .onStart {
                    _loadingSelects.value = true
                    _errorSelects.value = null
                }
                .catch { e ->
                    _loadingSelects.value = false
                    _errorSelects.value = e.message ?: "Error al cargar los selects"
                    _zonas.value = emptyList()
                    _clases.value = emptyList()
                    _tipos.value = emptyList()
                    _servicios.value = emptyList()
                }
                .collect { data ->
                    _zonas.value = data.zonas
                    _clases.value = data.clases
                    _tipos.value = data.tipos
                    _servicios.value = data.servicio
                    _loadingSelects.value = false
                }
        }
    }

    fun submitNewVehicle(
        context: Context,
        formData: VehicleRequestDto,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _creatingVehicle.value = true
                _errorcreatingVehicle.value = null

                val idClaseVehiculo = formData.idClaseVehiculo
                val idActividadVehiculo = formData.idActividadVehiculo
                val idZona = formData.idZona
                val marca = formData.marca
                val servicio = formData.servicio
                val capacidadVehiculo = formData.capacidadVehiculo
                val modeloVehiculo = formData.modeloVehiculo
                val color = formData.color
                val fechaMatricula = formData.fechaMatricula
                val placa = formData.placa
                Log.d("FormViewModel", "idClaseVehiculo: $idClaseVehiculo")
                Log.d("FormViewModel", "idActividadVehiculo: $idActividadVehiculo")
                Log.d("FormViewModel", "idZona: $idZona")
                Log.d("FormViewModel", "marca: $marca")
                Log.d("FormViewModel", "servicio: $servicio")
                Log.d("FormViewModel", "capacidadVehiculo: $capacidadVehiculo")
                Log.d("FormViewModel", "modeloVehiculo: $modeloVehiculo")
                Log.d("FormViewModel", "color: $color")
                Log.d("FormViewModel", "fechaMatricula: $fechaMatricula")
                Log.d("FormViewModel", "placa: $placa")

                repo.createVehicle(
                    VehicleRequestDto(
                        idClaseVehiculo = idClaseVehiculo,
                        idActividadVehiculo = idActividadVehiculo,
                        idZona = idZona,
                        marca = marca,
                        servicio = servicio,
                        capacidadVehiculo = capacidadVehiculo,
                        modeloVehiculo = modeloVehiculo,
                        color = color,
                        fechaMatricula = fechaMatricula,
                        placa = placa
                    )
                ).collect {
                    onSuccess()
                }

            } catch (e: Exception) {
                _errorcreatingVehicle.value = e.message ?: "Error al crear el vehículo"
                onError("Error: ${e.message}")
            } finally {
                _creatingVehicle.value = false
            }
        }
    }
}