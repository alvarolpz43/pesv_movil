package com.example.pesv_movil.preoperacional.Form


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.data.DataFormulario
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre
import com.example.pesv_movil.preoperacional.data.RequestBodyForm
import com.example.pesv_movil.preoperacional.data.RespuestaForm
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FormPreoperacionalViewModel(
    private val tokenManager: TokenManager,
    private val vehicleId: String
) : ViewModel() {


    private val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    private val _formulario = MutableStateFlow<DataFormulario?>(null)  // Inicializado como nulo
    val formulario: StateFlow<DataFormulario?> = _formulario

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Almacena las respuestas (preguntaId -> respuesta)

    val respuestas = mutableStateMapOf<String, Boolean>()


    init {
        viewModelScope.launch {
            cargarFormulario(vehicleId)
        }
    }

    private suspend fun cargarFormulario(vehicleId: String) {
        val userToken = tokenManager.token.first() ?: ""

        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFormById("Bearer $userToken", vehicleId)

                if (response.isSuccessful && response.body()?.success == true) {
                    val formularioList = response.body()?.formulario ?: emptyList()

                    if (formularioList.isNotEmpty()) {
                        _formulario.value = formularioList[0]  // Tomamos el primer formulario
                        Log.i("Form1", "Formulario cargado correctamente: ${_formulario.value}")
                    } else {
                        Log.w("Form1", "No se encontró ningún formulario para el vehículo.")
                    }
                } else {
                    Log.e("Form1", "Error en la respuesta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Form1", "Excepción: ${e.message}")
            } finally {
                _isLoading.value = false
                Log.i("Form1", "Carga finalizada, isLoading = ${_isLoading.value}")
            }
        }
    }
    private val _isSending = MutableStateFlow(false)


    private val _sendSuccess = MutableStateFlow<Boolean?>(null)
    val sendSuccess: StateFlow<Boolean?> = _sendSuccess








    fun enviarRespuestas(context: Context) = viewModelScope.launch {
        val userToken = tokenManager.token.first() ?: run {
            Log.e("FormViewModel", "No hay token disponible")
            return@launch
        }

        // Filtrar respuestas válidas
        val respuestasCompletadas = respuestas.mapNotNull { (preguntaId, respuesta) ->
            respuesta?.let { RespuestaForm(preguntaId, it) }
        }

        if (respuestasCompletadas.size != formulario.value?.preguntas?.size) {
            Log.e("FormViewModel", "No todas las preguntas están respondidas")
            return@launch
        }

        // Construir el objeto RequestBodyForm
        val requestBody = RequestBodyForm(
            formularioId = formulario.value?._id ?: "",
            idVehiculo = vehicleId ?: "",
            respuestas = respuestasCompletadas
        )

        // Log de respuestas enviadas
        Log.i("FormViewModel", "Enviando respuestas: $requestBody")

        _isSending.value = true
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.registerForm("Bearer $userToken", requestBody)
            }

            if (response.isSuccessful ) {
                _sendSuccess.value = true  // ✅ Formulario enviado con éxito
                Log.i("FormViewModel", "Respuestas enviadas correctamente")

            } else {
                _sendSuccess.value = false  // ❌ Error en el envío
                Log.e("FormViewModel", "Error al enviar: ${response.errorBody()?.string()}")

            }
        } catch (e: Exception) {
            _sendSuccess.value = false  // ❌ Error de red
            Log.e("FormViewModel", "Error de red: ${e.message}")
        } finally {
            _isSending.value = false
        }
    }

}