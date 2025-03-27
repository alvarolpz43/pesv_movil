package com.example.pesv_movil.preoperacional

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.data.DataPregunta
import com.example.pesv_movil.preoperacional.data.RequestBodyForm
import com.example.pesv_movil.preoperacional.data.ResponseFormById
import com.example.pesv_movil.preoperacional.data.RespuestaForm
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPreoperacionalScreen(
    navController: NavController,
    onClose: () -> Unit,
    tokenManager: TokenManager,
    vehicleId: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)
    val respuestasState = remember { mutableStateOf<List<RespuestaForm>>(emptyList()) }
    var idForm = remember { mutableStateOf("") }
    val context = LocalContext.current

        Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formulario Preoperacional") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        keyboardController?.hide()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {


                // **Asegurar que el formulario ocupe todo el espacio disponible**
                Column(
                    modifier = Modifier
                        .weight(1f) // Hace que la lista use el espacio disponible sin empujar el botón
                        .fillMaxSize()
                ) {
                    GetFormsById(
                        apiService = apiService,
                        tokenManager = tokenManager,
                        idVehicle = vehicleId,
                        idForm = idForm,
                        respuestasState = respuestasState
                    )
                }

                // **Botón fijo en la parte inferior**
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            submitResponseOfForm(
                                tokenManager = tokenManager,
                                apiService = apiService,
                                formRequest = RequestBodyForm(
                                    formularioId = idForm.value,
                                    idVehiculo = vehicleId,
                                    respuestas = respuestasState.value
                                ),
                                onClose = onClose,
                                context = context,
                                onSucces = { success ->
                                    if (success) {
                                        Log.d("Formulario", "Respuestas enviadas con éxito")
                                    } else {
                                        Log.e("Formulario", "Error al enviar respuestas")
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Enviar respuestas")
                }
            }
        }
    )
}


@Composable
fun GetFormsById(
    apiService: ApiService,
    tokenManager: TokenManager,
    idVehicle: String,
    idForm: MutableState<String>,
    respuestasState: MutableState<List<RespuestaForm>>
) {
    val formsResponse = produceState<ResponseFormById?>(
        initialValue = null
    ) {
        withContext(Dispatchers.IO) {
            try {
                val tokenValue = tokenManager.token.first() ?: ""
                val call = apiService.getFormById("Bearer $tokenValue", idVehicle)
                val response = call.execute()
                if (response.isSuccessful) {
                    value = response.body()
                    Log.d("FetchMyVehiculos", "Respuesta exitosa: ${response.body()}")
                } else {
                    Log.e("FetchMyVehiculos", "Error en la respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FetchMyVehiculos", "Error fetching vehicles: ${e.message}")
            }
        }
    }
    formsResponse.value?.formulario?.firstOrNull()?.let { form ->
        idForm.value = form._id
    }

    val responsePreguntas = formsResponse.value?.formulario?.flatMap { it.preguntas } ?: emptyList()

    if (formsResponse.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (responsePreguntas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay Formulario.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(responsePreguntas) { pregunta ->
                    CardForm(
                        form = pregunta,
                        message = "No hay preguntas",
                        onAnswerSelected = { idPregunta, respuesta ->
                            respuestasState.value = respuestasState.value.toMutableList().apply {
                                removeAll { it.idPregunta == idPregunta }
                                add(RespuestaForm(idPregunta, respuesta))
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun CardForm(
    form: DataPregunta?,
    message: String,
    onAnswerSelected: (String, Boolean) -> Unit
) {
    var selectedAnswer = remember { mutableStateOf<Boolean?>(null) }

    if (form != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = form.preguntaTexto,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedAnswer.value == true,
                        onCheckedChange = {
                            selectedAnswer.value = true
                            onAnswerSelected(form._id, true)
                        }
                    )
                    Text(text = "Sí", modifier = Modifier.padding(start = 4.dp))

                    Spacer(modifier = Modifier.width(24.dp))

                    Checkbox(
                        checked = selectedAnswer.value == false,
                        onCheckedChange = {
                            selectedAnswer.value = false
                            onAnswerSelected(form._id, false)
                        }
                    )
                    Text(text = "No", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

    } else {
        Text(text = message)
    }
}


suspend fun submitResponseOfForm(
    tokenManager: TokenManager,
    apiService: ApiService,
    formRequest: RequestBodyForm,
    onClose: () -> Unit,
    context: Context,
    onSucces: (Boolean) -> Unit
) {
    try {
        val response = apiService.registerForm(
            token = "Bearer ${tokenManager.token.first()}",
            body = formRequest
        )

        if (response.isSuccessful) {
            Log.d("Formulario", "Respuestas enviadas correctamente")
            withContext(Dispatchers.Main) {
                onSucces(true)
                Toast.makeText(context, "Formulario enviado con éxito", Toast.LENGTH_SHORT).show()
                onClose()
            }
        } else {
            Log.e("Formulario", "Error en la respuesta: ${response.code()}")
            withContext(Dispatchers.Main) {
                onSucces(false)
            }
        }
    } catch (e: Exception) {
        Log.e("Formulario", "Error al enviar formulario: ${e.message}")
        withContext(Dispatchers.Main) {
            onSucces(false)
        }
    }
}



