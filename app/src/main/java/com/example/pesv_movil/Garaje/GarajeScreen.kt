package com.example.pesv_movil.Garaje


import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.CloudUpload
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.pesv_movil.Garaje.data.FormDataDocumentation
import com.example.pesv_movil.Garaje.dto.vehicleDtos.InfoVehicleDto
import com.example.pesv_movil.Garaje.viewModels.GarajeViewModel
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.R
import com.example.pesv_movil.utils.GarajeTopAppBar
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun GarajeScreen(
    navController: NavHostController,
    openDrawer: () -> Unit,
    viewModel: GarajeViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { GarajeTopAppBar(openDrawer = openDrawer) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(PesvScreens.FORM_VEHICLE_SCREEN) },
                contentColor = Color.White,
                containerColor = Color.Blue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar vehículo",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(
                text = "Vehículos",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                FetchMyVehiculos(viewModel = viewModel)
            }
        }
    }
}


@Composable
fun FetchMyVehiculos(viewModel: GarajeViewModel = hiltViewModel()) {
    val vehiculos by viewModel.vehiculos.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()

    var showDocumentUploadModal by remember { mutableStateOf(false) }
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadVehicles()
    }

    val vehiclesInUse = remember(vehiculos) { vehiculos.filter { it.vehiculoEnUso } }
    val vehiclesAvailable = remember(vehiculos) { vehiculos.filter { !it.vehiculoEnUso } }

    when {
        isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        errorMsg != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Ocurrió un error: ${errorMsg}")
            }
        }

        vehiculos.isEmpty() -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay vehículos registrados.")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (vehiclesInUse.isNotEmpty()) {
                    item {
                        Text(
                            text = "Vehículos en uso",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(vehiclesInUse) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            onEdit = {},
                            onDelete = {},
                            onChangeStatus = {
                                viewModel.changeStateVehicle(vehicle._id) {
                                    viewModel.loadVehicles()
                                }
                            },
                            onUploadDocuments = {},
                            message = "No hay Vehículos en uso"
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }

                item {
                    Text(
                        text = "Vehículos disponibles",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (vehiclesAvailable.isNotEmpty()) {
                    items(vehiclesAvailable) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            onEdit = {},
                            onChangeStatus = {
                                viewModel.changeStateVehicle(vehicle._id) {
                                    viewModel.loadVehicles()
                                }
                            },
                            onDelete = {},
                            onUploadDocuments = {
                                selectedVehicleId = vehicle._id
                                showDocumentUploadModal = true
                            },
                            message = "No hay Vehículos registrados"
                        )
                    }
                } else {
                    item { Text("No hay vehículos disponibles") }
                }
            }
        }
    }

    if (showDocumentUploadModal) {
        DocumentUploadModal(
            onDismissRequest = { showDocumentUploadModal = false },
            onServicioSelected = { /* opcional */ },
            idVehicle = selectedVehicleId,
            viewModel = viewModel
        )
    }
}



@Composable
fun VehicleCard(
    vehicle: InfoVehicleDto?,
    onEdit: () -> Unit,
    onChangeStatus: () -> Unit,
    onDelete: () -> Unit,
    onUploadDocuments: () -> Unit,
    message: String,
) {
    if (vehicle != null) {
        Card(
            modifier = Modifier
                .border(
                    1.dp,
                    colorResource(id = R.color.black),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {

            ConstraintLayout(
                modifier = Modifier
                    .padding(10.dp)
            ) {

                val (title, subTitle, infoVehicule) = createRefs()

                Row(
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)

                        }

                ) {

                    Text(
                        modifier = Modifier,
                        text = "${vehicle.marca} ${vehicle.modeloVehiculo}",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Row(
                    modifier = Modifier
                        .padding(3.dp)
                        .constrainAs(subTitle) {
                            top.linkTo(title.bottom)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono según el tipo de vehículo
                    val iconRes = if (vehicle.idClaseVehiculo == "67a50fff122183dc3aaddbae") {
                        R.drawable.ic_moto
                    } else {
                        R.drawable.ic_garage
                    }

                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )


                    Box(
                        modifier = Modifier
                            .background(
                                Color(0xFFFFEB3B),
                                shape = RoundedCornerShape(8.dp)
                            ) // Amarillo suave con bordes redondeados
                            .border(
                                1.dp,
                                colorResource(id = R.color.black),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = vehicle.placa,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Tipo de vehículo
                    Text(
                        modifier = Modifier.padding(start = 3.dp),
                        text = vehicle.idActividadVehiculo.nombreTipo,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(modifier = Modifier.weight(1f)) {

                        Text(
                            text = "Color: ${vehicle.color}",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Estado: ${if (vehicle.estadoVehiculo) "Si" else "No"}",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Capacidad: ${vehicle.capacidadVehiculo}",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Zona: ${vehicle.idZona.nombreZona}",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onChangeStatus,
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.primary))

                    ) {
                        Icon(Icons.Filled.ChangeCircle, contentDescription = "Cambiar estado")
                        if (LocalConfiguration.current.screenWidthDp > 360) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar")
                        }
                    }

                    Button(
                        onClick = onUploadDocuments,
                        modifier = Modifier
                            .weight(1f),
                        enabled = !vehicle.vehiculoEnUso,
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.secondary))
                    ) {
                        Icon(Icons.Filled.CloudUpload, contentDescription = "Cargar Documentos")
                        if (LocalConfiguration.current.screenWidthDp > 360) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cargar")
                        }
                    }
                }
            }
        }
    } else {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadModal(
    onDismissRequest: () -> Unit,
    onServicioSelected: (String?) -> Unit,
    idVehicle: String?,
    viewModel: GarajeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Inputs
    var selectedDate by remember { mutableStateOf("") }
    var numberInput by remember { mutableStateOf(TextFieldValue("")) }
    var pdfFilePath by remember { mutableStateOf<Uri?>(null) }

    // VM states
    val tiposDocumento by viewModel.typesDocVehicle.collectAsState()
    val loadingTypes by viewModel.loadingTypes.collectAsState(initial = false)
    val errorTypes by viewModel.errorTypes.collectAsState(initial = null)

    val uploading by viewModel.uploadingDoc.collectAsState(initial = false)
    val errorUpload by viewModel.errorUploadDoc.collectAsState(initial = null)

    // Selection
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(tiposDocumento.firstOrNull()) }

    // Cuando cambie la lista de tipos (ej. al terminar de cargar), fijar una selección por defecto
    LaunchedEffect(tiposDocumento) {
        if (selectedOption == null && tiposDocumento.isNotEmpty()) {
            selectedOption = tiposDocumento.first()
            onServicioSelected(selectedOption?._id)
        }
    }

    // Fecha
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day -> selectedDate = "$day/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // PDF picker
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                pdfFilePath = it
                Toast.makeText(context, "Documento PDF seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Cargar tipos al abrir
    LaunchedEffect(Unit) {
        viewModel.loadTypesDocsFully()
    }

    AlertDialog(
        onDismissRequest = {
            if (!uploading) onDismissRequest()
        },
        title = { Text("Cargar Documento") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Estado de carga/errores de tipos
                when {
                    loadingTypes -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        }
                        Text(
                            "Cargando tipos de documento...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    errorTypes != null -> {
                        Text(
                            errorTypes ?: "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Tipo documento
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        if (!loadingTypes && tiposDocumento.isNotEmpty()) {
                            expanded = !expanded
                        }
                    }
                ) {
                    OutlinedTextField(
                        value = selectedOption?.nombre ?: if (loadingTypes) "Cargando..." else "",
                        onValueChange = {},
                        readOnly = true,
                        enabled = !loadingTypes && tiposDocumento.isNotEmpty() && !uploading,
                        label = { Text("Selecciona el tipo de documento") },
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
                        tiposDocumento.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.nombre) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                    onServicioSelected(option._id)
                                }
                            )
                        }
                    }
                }

                // Fecha
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uploading
                ) {
                    Text(if (selectedDate.isEmpty()) "Selecciona fecha" else selectedDate)
                }

                // Número
                OutlinedTextField(
                    value = numberInput,
                    onValueChange = { numberInput = it },
                    label = { Text("Número del documento") },
                    enabled = !uploading,
                    modifier = Modifier.fillMaxWidth()
                )

                // PDF
                Button(
                    onClick = { openFileLauncher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uploading
                ) {
                    Text("Seleccionar Documento PDF")
                }

                pdfFilePath?.let {
                    Text(
                        text = "Seleccionado: ${it.lastPathSegment}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Error de subida (si ocurrió)
                errorUpload?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val ok = selectedOption != null &&
                            selectedDate.isNotEmpty() &&
                            numberInput.text.isNotEmpty() &&
                            pdfFilePath != null &&
                            !idVehicle.isNullOrBlank()

                    if (ok) {
                        coroutineScope.launch {
                            viewModel.submitDocumentVehicle(
                                context = context,
                                formData = FormDataDocumentation(
                                    tipoDocumentoId = selectedOption!!._id,
                                    idVehiculo = idVehicle!!,
                                    fechaExpiracion = selectedDate,
                                    numeroDocumento = numberInput.text,
                                    documento = pdfFilePath
                                ),
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "✅ Documento cargado con éxito",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onDismissRequest()
                                },
                                onError = { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    } else {
                        Toast.makeText(context, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !uploading && !loadingTypes // bloquea mientras carga tipos o sube
            ) {
                if (uploading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Subiendo…")
                    }
                } else {
                    Text("Subir Documento")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !uploading
            ) {
                Text("Cancelar")
            }
        }
    )
}












