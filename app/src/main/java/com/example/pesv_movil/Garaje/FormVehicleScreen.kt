package com.example.pesv_movil.Garaje

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pesv_movil.Garaje.data.VehiculeRequest
import com.example.pesv_movil.components.MyResponseSelects
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.data.repositories.SelectsRepository
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormVehicleScreen(navController: NavController, onClose: () -> Unit) {
    val tokenManager = TokenManager(LocalContext.current)
    val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isSuccessful by remember { mutableStateOf(false) }


    var tipoSeleccionadoId by remember { mutableStateOf<String?>(null) }
    var zonaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var claseSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var servicioSeleccionadoId by remember { mutableStateOf<String?>(null) }
    val marcaSeleccionada = remember { mutableStateOf("") }
    val modeloSeleccionado = remember { mutableStateOf<Int?>(null) }
    val colorSeleccionado = remember { mutableStateOf("") }
    val placaSeleccionada = remember { mutableStateOf("") }
    val capacidadSeleccionada = remember { mutableStateOf<Int?>(null) }
    val matriculaSeleccionada = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Registro Vehículo") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        MarcaInput(marcaSeleccionada)
                        Spacer(modifier = Modifier.height(16.dp))
                        ModeloInput(modeloSeleccionado)
                        Spacer(modifier = Modifier.height(16.dp))
                        ColorInput(colorSeleccionado)
                        Spacer(modifier = Modifier.height(16.dp))
                        PlacaInput(placaSeleccionada)
                        Spacer(modifier = Modifier.height(16.dp))
                        CapacidadInput(capacidadSeleccionada)
                        Spacer(modifier = Modifier.height(16.dp))
                        MatriculaInput(matriculaSeleccionada)
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectTipoVehiculo(
                            tokenManager = tokenManager,
                            apiService = apiService,
                            onTipoSelected = { tipoSeleccionadoId = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectZona(
                            tokenManager = tokenManager,
                            apiService = apiService,
                            onZonaSelected = { zonaSeleccionadaId = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectClases(
                            tokenManager = tokenManager,
                            apiService = apiService,
                            onClaseSelected = { claseSeleccionadaId = it }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SelectServicio(
                            tokenManager = tokenManager,
                            apiService = apiService,
                            onServicioSelected = { servicioSeleccionadoId = it }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {

                        if (claseSeleccionadaId == null || tipoSeleccionadoId == null || zonaSeleccionadaId == null || servicioSeleccionadoId == null || marcaSeleccionada.value.isEmpty() || capacidadSeleccionada.value == null || modeloSeleccionado.value == null || colorSeleccionado.value.isEmpty() || matriculaSeleccionada.value.isEmpty() || placaSeleccionada.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Por favor, complete todos los campos",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isLoading.value = true
                        coroutineScope.launch {
                            submitDeliveryForm(
                                tokenManager = tokenManager,
                                apiService = apiService,
                                onClose = onClose,
                                context = context,
                                vehicleRequest = VehiculeRequest(
                                    marca = marcaSeleccionada.value,
                                    modeloVehiculo = modeloSeleccionado.value!!,
                                    color = colorSeleccionado.value,
                                    placa = placaSeleccionada.value,
                                    capacidadVehiculo = capacidadSeleccionada.value!!,
                                    fechaMatricula = matriculaSeleccionada.value,
                                    servicio = servicioSeleccionadoId ?: "",
                                    idActividadVehiculo = tipoSeleccionadoId ?: "",
                                    idClaseVehiculo = claseSeleccionadaId ?: "",
                                    idZona = zonaSeleccionadaId ?: ""
                                ),
                                onSucces = { success ->
                                    showSuccessDialog = success
                                }

                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Registrar Vehículo")
                    }

                    if (showSuccessDialog) {
                        AlertDialogSucces(
                            onConfirmation = { showSuccessDialog = false },
                            dialogTitle = "Éxito",
                            dialogText = "Vehículo registrado con éxito",
                            icon = Icons.Default.Check
                        )
                    }

                }
            }
        }
    )
}


suspend fun submitDeliveryForm(
    tokenManager: TokenManager,
    apiService: ApiService,
    vehicleRequest: VehiculeRequest,
    onClose: () -> Unit,
    context: Context,
    onSucces: (Boolean) -> Unit
) {
    val marca = vehicleRequest.marca
    val modelo = vehicleRequest.modeloVehiculo
    val color = vehicleRequest.color
    val placa = vehicleRequest.placa
    val capacidad = vehicleRequest.capacidadVehiculo
    val fechaMatricula = vehicleRequest.fechaMatricula
    val servicio = vehicleRequest.servicio
    val tipo = vehicleRequest.idActividadVehiculo
    val clase = vehicleRequest.idClaseVehiculo
    val zona = vehicleRequest.idZona


    Log.d("submitDeliveryForm", "marca: $marca")
    Log.d(
        "submitDeliveryForm",
        "modelo: $modelo"
    )
    Log.d("submitDeliveryForm", "color: ${color}")
    Log.d("submitDeliveryForm", "placa: ${placa}")
    Log.d("submitDeliveryForm", "capacidad: ${capacidad}")
    Log.d("submitDeliveryForm", "fechaMatricula: ${fechaMatricula}")
    Log.d("submitDeliveryForm", "servicio: ${servicio}")
    Log.d("submitDeliveryForm", "tipo: ${tipo}")
    Log.d("submitDeliveryForm", "clase: ${clase}")
    Log.d("submitDeliveryForm", "zona: ${zona}")

    val response = apiService.registerVehicle(
        token = "Bearer ${tokenManager.token.first()}",
        body = VehiculeRequest(
            marca = marca,
            modeloVehiculo = modelo,
            color = color,
            placa = placa,
            capacidadVehiculo = capacidad,
            fechaMatricula = fechaMatricula,
            servicio = servicio,
            idActividadVehiculo = tipo,
            idClaseVehiculo = clase,
            idZona = zona
        )
    )

    if (response.isSuccessful) {
        onClose()
        val responseBody = response.body()?.toString() ?: "Sin Contenido en el cuerpo"
        val responseCode = response.code()
        val responseHeaders = response.headers().toMultimap()

        Log.d(
            "submitDeliveryForm", """
        ✅ Formulario enviado con éxito:
        - Código HTTP: $responseCode
        - Cuerpo de respuesta: $responseBody
        - Encabezados de respuesta: $responseHeaders
    """.trimIndent()
        )

        onSucces(true)

    } else {
        val errorBody = response?.errorBody()?.string() ?: "Sin detalles de error"
        val errorCode = response.code() ?: "Codigo no disponible"
        val errorHeaders = response?.headers()?.toMultimap() ?: "Sin encabezados"


        Log.e(
            "submitDeliveryForm", """
        ❌ Error al enviar el formulario:
        - Código HTTP: $errorCode
        - Detalles del error: $errorBody
        - Encabezados de error: $errorHeaders
    """.trimIndent()
        )
    }
}

@Composable
fun MarcaInput(marca: MutableState<String>) {
    Column() {
        Text(
            text = "Marca",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = marca.value,
            onValueChange = { marca.value = it },
            label = { Text("Ingrese la Marca") },
            modifier = Modifier.fillMaxWidth()
        )
    }


}

@Composable
fun AlertDialogSucces(
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onConfirmation()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        }
    )
}

@Composable
fun ModeloInput(modelo: MutableState<Int?>) {
    val textValue = remember { mutableStateOf(modelo.value?.toString() ?: "") }

    Column {
        Text(
            text = "Modelo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = textValue.value,
            onValueChange = { newText ->
                textValue.value = newText
                modelo.value = newText.toIntOrNull()
            },
            label = { Text("Ingrese el Modelo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            )
        )
    }
}


@Composable
fun ColorInput(color: MutableState<String>) {
    Column() {
        Text(
            text = "Color",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = color.value,
            onValueChange = { color.value = it },
            label = { Text("Ingrese el Color") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            )
        )
    }


}

@Composable
fun PlacaInput(placa: MutableState<String>) {
    Column() {
        Text(
            text = "Placa",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = placa.value,
            onValueChange = { placa.value = it },
            label = { Text("Ingrese la Placa") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            )
        )
    }


}

@Composable
fun CapacidadInput(capacidad: MutableState<Int?>) {


    val textValue = remember { mutableStateOf(capacidad.value?.toString() ?: "") }
    Column() {
        Text(
            text = "Capacidad",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = textValue.value,
            onValueChange = { newText ->
                textValue.value = newText
                capacidad.value = newText.toIntOrNull()

            },
            label = { Text("Ingrese la Capacidad") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            )
        )
    }


}


@Composable
fun MatriculaInput(matricula: MutableState<String>) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val showDatePicker = {
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val currentCalendar = Calendar.getInstance()

                if (selectedCalendar.after(currentCalendar)) {
                    Toast.makeText(
                        context,
                        "La fecha de matrícula no puede ser futura",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val formattedDate = isoFormat.format(selectedCalendar.time)
                    matricula.value = formattedDate
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Column {
        Text(
            text = "Fecha de Matrícula",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = { showDatePicker() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (matricula.value.isEmpty()) {
                    Text(
                        "Selecciona una fecha",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                } else {
                    Text(
                        matricula.value,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectServicio(
    tokenManager: TokenManager,
    apiService: ApiService,
    onServicioSelected: (String?) -> Unit
) {

    val repository = remember { SelectsRepository(apiService, tokenManager) }
    val responseServicio by produceState<MyResponseSelects?>(initialValue = null) {

        withContext(Dispatchers.IO) {
            value = repository.fetchSelectData()
        }
    }
    val options = responseServicio?.servicio
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options?.firstOrNull()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.name ?: "",
            onValueChange = { selectedOption?._id },
            readOnly = true,
            label = { Text("Selecciona la Actividad") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onServicioSelected(option._id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectZona(
    tokenManager: TokenManager,
    apiService: ApiService,
    onZonaSelected: (String?) -> Unit
) {

    val repository = remember { SelectsRepository(apiService, tokenManager) }
    val responseZona by produceState<MyResponseSelects?>(initialValue = null) {

        withContext(Dispatchers.IO) {
            value = repository.fetchSelectData()
        }
    }
    val options = responseZona?.zonas
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options?.firstOrNull()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.nombreZona ?: "",
            onValueChange = { selectedOption?._id },
            readOnly = true,
            label = { Text("Selecciona la Zona") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.nombreZona) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onZonaSelected(option._id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectClases(
    tokenManager: TokenManager,
    apiService: ApiService,
    onClaseSelected: (String?) -> Unit
) {

    val repository = remember { SelectsRepository(apiService, tokenManager) }
    val responseClase by produceState<MyResponseSelects?>(initialValue = null) {

        withContext(Dispatchers.IO) {
            value = repository.fetchSelectData()
        }
    }
    val options = responseClase?.clases
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options?.firstOrNull()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.name ?: "",
            onValueChange = { selectedOption?._id },
            readOnly = true,
            label = { Text("Selecciona la Clase") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onClaseSelected(option._id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTipoVehiculo(
    tokenManager: TokenManager,
    apiService: ApiService,
    onTipoSelected: (String?) -> Unit
) {

    val repository = remember { SelectsRepository(apiService, tokenManager) }
    val responseTipo by produceState<MyResponseSelects?>(initialValue = null) {

        withContext(Dispatchers.IO) {
            value = repository.fetchSelectData()
        }
    }
    val options = responseTipo?.tipos
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options?.firstOrNull()) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption?.nombreTipo ?: "",
            onValueChange = { selectedOption?._id },
            readOnly = true,
            label = { Text("Selecciona la Actividad") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.nombreTipo) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                        onTipoSelected(option._id)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormScreenPreview() {
    FormVehicleScreen(navController = NavController(LocalContext.current), onClose = {})
}
