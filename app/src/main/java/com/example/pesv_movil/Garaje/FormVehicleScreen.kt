package com.example.pesv_movil.Garaje

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesv_movil.Garaje.data.VehiculeRequest
import com.example.pesv_movil.Garaje.dto.vehicleDtos.VehicleRequestDto
import com.example.pesv_movil.Garaje.viewModels.FormViewModel
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
fun FormVehicleScreen(
    navController: NavController,
    onClose: () -> Unit,
    viewModel: FormViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State desde el VM
    val zonas by viewModel.zonas.collectAsState()
    val clases by viewModel.clases.collectAsState()
    val tipos by viewModel.tipos.collectAsState()
    val servicios by viewModel.servicios.collectAsState()

    val loadingSelects by viewModel.loadingSelects.collectAsState()
    val creatingVehicle by viewModel.creatingVehicle.collectAsState()
    val errorSelects by viewModel.errorSelects.collectAsState()
    val errorCreating by viewModel.errorcreatingVehicle.collectAsState()

    // Campos del formulario (UI state)
    var idTipo by remember { mutableStateOf<String?>(null) }
    var idZona by remember { mutableStateOf<String?>(null) }
    var idClase by remember { mutableStateOf<String?>(null) }
    var idServicio by remember { mutableStateOf<String?>(null) }

    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") } // lo convertimos a Int? al enviar
    var color by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var capacidad by remember { mutableStateOf("") } // lo convertimos a Int? al enviar
    var fechaMatricula by remember { mutableStateOf("") } // ISO string

    var showSuccessDialog by remember { mutableStateOf(false) }

    // Cargar selects al abrir
    LaunchedEffect(Unit) {
        viewModel.loadSelectsData()
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Registro Vehículo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Estado de carga / error de selects
            if (loadingSelects) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Cargando opciones…")
                }
                Spacer(Modifier.height(12.dp))
            }
            errorSelects?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Marca
                    LabeledTextField(
                        label = "Marca",
                        value = marca,
                        onValueChange = { marca = it }
                    )
                    Spacer(Modifier.height(16.dp))

                    // Modelo (numérico)
                    LabeledTextField(
                        label = "Modelo",
                        value = modelo,
                        onValueChange = { modelo = it },
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(16.dp))

                    // Color
                    LabeledTextField(
                        label = "Color",
                        value = color,
                        onValueChange = { color = it }
                    )
                    Spacer(Modifier.height(16.dp))

                    // Placa
                    LabeledTextField(
                        label = "Placa",
                        value = placa,
                        onValueChange = { placa = it }
                    )
                    Spacer(Modifier.height(16.dp))

                    // Capacidad (numérico)
                    LabeledTextField(
                        label = "Capacidad",
                        value = capacidad,
                        onValueChange = { capacidad = it },
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(16.dp))

                    // Fecha Matrícula (DatePicker -> ISO)
                    MatriculaPicker(
                        value = fechaMatricula,
                        onValue = { fechaMatricula = it }
                    )
                    Spacer(Modifier.height(16.dp))

                    // Selects (desde VM)
                    SelectDropdown(
                        label = "Actividad (Tipo de vehículo)",
                        options = tipos.map { it._id to it.nombreTipo },
                        selectedId = idTipo,
                        onSelected = { idTipo = it },
                        enabled = !loadingSelects
                    )
                    Spacer(Modifier.height(16.dp))

                    SelectDropdown(
                        label = "Zona",
                        options = zonas.map { it._id to it.nombreZona },
                        selectedId = idZona,
                        onSelected = { idZona = it },
                        enabled = !loadingSelects
                    )
                    Spacer(Modifier.height(16.dp))

                    SelectDropdown(
                        label = "Clase",
                        options = clases.map { it._id to it.name },
                        selectedId = idClase,
                        onSelected = { idClase = it },
                        enabled = !loadingSelects
                    )
                    Spacer(Modifier.height(16.dp))

                    SelectDropdown(
                        label = "Servicio",
                        options = servicios.map { it._id to it.name },
                        selectedId = idServicio,
                        onSelected = { idServicio = it },
                        enabled = !loadingSelects
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón Enviar
            Button(
                onClick = {
                    // Validación simple local
                    val modeloString = modelo.toString()
                    val capInt = capacidad.toIntOrNull()

                    val hayVacios = listOf(
                        marca.isBlank(),
                        modeloString == "",
                        color.isBlank(),
                        placa.isBlank(),
                        capInt == null,
                        fechaMatricula.isBlank(),
                        idServicio.isNullOrBlank(),
                        idTipo.isNullOrBlank(),
                        idClase.isNullOrBlank(),
                        idZona.isNullOrBlank()
                    ).any { it }

                    if (hayVacios) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    viewModel.submitNewVehicle(
                        context = context,
                        formData = VehicleRequestDto(
                            idClaseVehiculo = idClase!!,
                            idActividadVehiculo = idTipo!!,
                            idZona = idZona!!,
                            marca = marca,
                            servicio = idServicio!!,
                            capacidadVehiculo = capInt!!,
                            modeloVehiculo = modeloString,
                            color = color,
                            fechaMatricula = fechaMatricula,
                            placa = placa
                        ),
                        onSuccess = {
                            showSuccessDialog = true
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !creatingVehicle && !loadingSelects
            ) {
                if (creatingVehicle) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrar Vehículo")
                }
            }

            errorCreating?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            if (showSuccessDialog) {
                AlertDialogSucces(
                    onConfirmation = {
                        showSuccessDialog = false
                        onClose()
                        navController.popBackStack()
                    },
                    dialogTitle = "Éxito",
                    dialogText = "Vehículo registrado con éxito",
                    icon = Icons.Default.Check
                )
            }
        }
    }
}

@Composable
private fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Ingrese $label") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = keyboardType
            )
        )
    }
}

@Composable
private fun MatriculaPicker(
    value: String,
    onValue: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val showDatePicker = {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply { set(year, month, day) }
                val now = Calendar.getInstance()
                if (selected.after(now)) {
                    Toast.makeText(
                        context,
                        "La fecha de matrícula no puede ser futura",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    onValue(fmt.format(selected.time))
                }
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
        Button(
            onClick = showDatePicker,
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
                if (value.isEmpty()) {
                    Text(
                        "Selecciona una fecha",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                } else {
                    Text(value, modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectDropdown(
    label: String,
    options: List<Pair<String, String>>, // (id, texto)
    selectedId: String?,
    onSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = options.firstOrNull { it.first == selectedId }?.second ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled && options.isNotEmpty()) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            enabled = enabled && options.isNotEmpty(),
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (id, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(id)
                        expanded = false
                    }
                )
            }
        }
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









