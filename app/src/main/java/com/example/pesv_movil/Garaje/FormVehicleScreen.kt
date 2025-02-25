package com.example.pesv_movil.Garaje

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pesv_movil.components.MyResponseSelects
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.data.repositories.SelectsRepository
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormVehicleScreen(navController: NavController) {
    val tokenManager = TokenManager(LocalContext.current)
    val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    var tipoSeleccionadoId by remember { mutableStateOf<String?>(null) }
    var zonaSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var claseSeleccionadaId by remember { mutableStateOf<String?>(null) }
    var servicioSeleccionadoId by remember { mutableStateOf<String?>(null) }
    val marcaSeleccionada = remember { mutableStateOf("") }
    val modeloSeleccionado = remember { mutableStateOf("") }
    val colorSeleccionado = remember { mutableStateOf("") }
    val placaSeleccionada = remember { mutableStateOf("") }
    val capacidadSeleccionada = remember { mutableStateOf("") }
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
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                androidx.compose.material3.Button(
                    onClick = {
                        // Aquí puedes validar y enviar los datos o navegar a otra pantalla.
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar Vehículo")
                }
            }
        }
    )
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
            onValueChange = {marca.value = it},
            label = { Text("Ingrese la Marca") },
            modifier = Modifier.fillMaxWidth()
        )
    }


}

@Composable
fun ModeloInput(modelo: MutableState<String>) {
    Column() {
        Text(
            text = "Modelo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = modelo.value,
            onValueChange = {modelo.value = it},
            label = { Text("Ingrese el Modelo") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number)
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
            onValueChange = {color.value = it},
            label = { Text("Ingrese el Color") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text)
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
            onValueChange = {placa.value = it},
            label = { Text("Ingrese la Placa") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text)
        )
    }


}

@Composable
fun CapacidadInput(capacidad: MutableState<String>) {
    Column() {
        Text(
            text = "Capacidad",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = capacidad.value,
            onValueChange = {capacidad.value = it},
            label = { Text("Ingrese la Capacidad") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number)
        )
    }


}




@Composable
fun MatriculaInput(matricula: MutableState<String>)  {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
                matricula.value = formattedDate
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column {
        Text(
            text = "Fecha de Matrícula",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = matricula.value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Selecciona una fecha") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            keyboardOptions = KeyboardOptions.Default
        )
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
            label = { Text("Selecciona el Servicio") },
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
            label = { Text("Selecciona el Tipo") },
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
    FormVehicleScreen(navController = NavController(LocalContext.current))
}
