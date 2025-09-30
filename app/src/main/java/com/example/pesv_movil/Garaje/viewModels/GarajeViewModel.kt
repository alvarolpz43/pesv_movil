package com.example.pesv_movil.Garaje.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.Garaje.data.FormDataDocumentation
import com.example.pesv_movil.Garaje.dto.userDtos.DataTipoDctoUsuarioDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.DataTipoDctoVehicleDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.InfoVehicleDto
import com.example.pesv_movil.Garaje.usecases.GarajeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GarajeViewModel @Inject constructor(
    private val useCase: GarajeUseCase
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<InfoVehicleDto>>(emptyList())
    val vehiculos: StateFlow<List<InfoVehicleDto>> = _vehicles

    private val _typeVehicle = MutableStateFlow<List<DataTipoDctoVehicleDto>>(emptyList())
    val typesDocVehicle: StateFlow<List<DataTipoDctoVehicleDto>> = _typeVehicle

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loadingTypes = MutableStateFlow(false)
    val loadingTypes: StateFlow<Boolean> = _loadingTypes

    private val _errorTypes = MutableStateFlow<String?>(null)
    val errorTypes: StateFlow<String?> = _errorTypes

    private val _changingStateIds = MutableStateFlow<Set<String>>(emptySet())
    val changingStateIds: StateFlow<Set<String>> = _changingStateIds

    private val _errorChangeState = MutableStateFlow<String?>(null)
    val errorChangeState: StateFlow<String?> = _errorChangeState

    private val _uploadingDoc = MutableStateFlow(false)
    val uploadingDoc: StateFlow<Boolean> = _uploadingDoc

    private val _errorUploadDoc = MutableStateFlow<String?>(null)
    val errorUploadDoc: StateFlow<String?> = _errorUploadDoc


    private val _typesDocUser = MutableStateFlow<List<DataTipoDctoUsuarioDto>>(emptyList())
    val typesDocUser: StateFlow<List<DataTipoDctoUsuarioDto>> = _typesDocUser

    private val _isExisiting = MutableStateFlow<Boolean>(false)
    val isExisiting: StateFlow<Boolean> = _isExisiting

    fun loadVehicles() {
        viewModelScope.launch {
            useCase.getMyVechicles()
                .onStart {
                    _loading.value = true
                    _error.value = null
                }
                .catch { e ->
                    _loading.value = false
                    _vehicles.value = emptyList()
                    _error.value = e.message ?: "Error cargando vehículos"
                }
                .collect { list ->
                    _vehicles.value = list
                    _loading.value = false
                }
        }
    }


    fun loadTypesDocsFully() {
        viewModelScope.launch {
            useCase.getTypeDocumentVehiclesAndUser()
                .onStart {
                    _loadingTypes.value = true
                    _errorTypes.value = null
                }
                .catch { e ->
                    _loadingTypes.value = false
                    _errorTypes.value = e.message ?: "Error cargando tipos de documento"
                    _typesDocUser.value = emptyList()
                    _typeVehicle.value = emptyList()
                }
                .collect { data ->
                    _typesDocUser.value = data.tipoDocUsuario
                    _typeVehicle.value = data.tipoDocVehiculo
                    _loadingTypes.value = false
                }
        }
    }

    fun getDocumentsVehicle(id: String) {
        viewModelScope.launch {
            useCase.getDocumentsVehicle(id).collect { list ->
                _isExisiting.value = list.isNotEmpty()
            }
        }
    }

    fun changeStateVehicle(idVehiculo: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                _changingStateIds.value = _changingStateIds.value + idVehiculo
                _errorChangeState.value = null
                useCase.changeStateVehicle(idVehiculo).collect {
                    onSuccess()
                }
            } catch (e: Exception) {
                _errorChangeState.value = e.message ?: "Error al cambiar estado"
            } finally {
                _changingStateIds.value = _changingStateIds.value - idVehiculo
            }
        }
    }

    fun submitDocumentVehicle(
        context: Context,
        formData: FormDataDocumentation,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uploadingDoc.value = true
                _errorUploadDoc.value = null

                val isExist = validateDocumentType(
                    formData.idVehiculo, formData.tipoDocumentoId, context
                )
                if (isExist) {
                    onError("⚠️ El tipo de documento ya existe en el vehículo")
                    return@launch
                }

                val idTipoDoc = formData.tipoDocumentoId.toRequestBody("text/plain".toMediaType())
                val idVehiculo = formData.idVehiculo.toRequestBody("text/plain".toMediaType())
                val fechaExpiracion =
                    formData.fechaExpiracion.toRequestBody("text/plain".toMediaType())
                val numeroDocumento =
                    formData.numeroDocumento.toRequestBody("text/plain".toMediaType())

                val documentPart = formData.documento?.let { fileUri ->
                    val file = copyUriToTempFile(context, fileUri)
                    val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("documento", file.name, requestFile)
                }

                useCase.submitDocumentVehicle(
                    documento = documentPart,
                    tipoDocumentoId = idTipoDoc,
                    idVehiculo = idVehiculo,
                    fechaExpiracion = fechaExpiracion,
                    numeroDocumento = numeroDocumento
                ).collect {
                    onSuccess()
                }
            } catch (e: Exception) {
                _errorUploadDoc.value = e.message ?: "Error subiendo documento"
                onError("Error: ${e.message}")
            } finally {
                _uploadingDoc.value = false
            }
        }
    }


    private suspend fun validateDocumentType(
        idVehiculo: String,
        idTipoDoc: String,
        context: Context
    ): Boolean {
        return try {
            val response = useCase.getDocumentsVehicle(idVehiculo).firstOrNull()
            val idTipoDocList = response?.mapNotNull { it.tipoDocumentoId._id } ?: emptyList()
            idTipoDocList.contains(idTipoDoc)
        } catch (e: Exception) {
            Log.e("GarajeViewModel", "Error al validar documento: ${e.message}")
            false
        }
    }

    private fun copyUriToTempFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("No se pudo abrir el URI: $uri")

        val tempFile = File(context.cacheDir, "temp_document.pdf")
        tempFile.outputStream().use { output -> inputStream.copyTo(output) }
        return tempFile
    }


}