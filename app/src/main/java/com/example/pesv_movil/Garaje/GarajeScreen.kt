package com.example.pesv_movil.Garaje


import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.pesv_movil.Garaje.data.InfoVehicle
import com.example.pesv_movil.Garaje.data.MyResponseVehiculo
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.components.MyResponseTipoDctoVehicle
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.data.repositories.DocsVehiclesRepository
import com.example.pesv_movil.data.repositories.TipoDocumentoRepository
import com.example.pesv_movil.utils.GarajeTopAppBar
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.Calendar

@Composable
fun GarajeScreen(
    navController: NavHostController,
    openDrawer: () -> Unit,
    tokenManager: TokenManager
) {
    val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { GarajeTopAppBar(openDrawer = openDrawer) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(PesvScreens.FORM_VEHICLE_SCREEN) },
                containerColor = MaterialTheme.colorScheme.primary
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
                FetchMyVehiculos(tokenManager = tokenManager, apiService = apiService)
            }
        }
    }
}

@Composable
fun FetchMyVehiculos(tokenManager: TokenManager, apiService: ApiService) {
    val vehiclesResponse by produceState<MyResponseVehiculo?>(initialValue = null) {
        withContext(Dispatchers.IO) {
            try {
                val tokenValue = tokenManager.token.first() ?: ""
                val userId = tokenManager.getUserIdBlocking() ?: ""
                Log.d("FetchMyVehiculosId", "ID del usuario: $userId")

                val call = apiService.getMyVehiculos("Bearer $tokenValue")
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

    var showDocumentUploadModal by remember { mutableStateOf(false) }
    var servicioSeleccionadoId by remember { mutableStateOf<String?>(null) }
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }


    if (vehiclesResponse == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        if (vehiclesResponse!!.data.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay vehículos registrados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vehiclesResponse!!.data) { vehicle ->
                    VehicleCard(vehicle = vehicle, onEdit = {}, onDelete = {}, onUploadDocuments = {
                        selectedVehicleId = vehicle._id
                        showDocumentUploadModal = true
                    })
                }
            }
        }
    }

    if (showDocumentUploadModal) {
        DocumentUploadModal(
            onDismissRequest = { showDocumentUploadModal = false },
            onUploadClick = { type, date, number, path ->
                Log.d(
                    "DocumentUploadModal",
                    "Tipo: $type, Fecha: $date, Número: $number, PDF: $path"
                )
                showDocumentUploadModal = false
            },
            apiService = apiService,
            tokenManager = tokenManager,
            onServicioSelected = { servicioSeleccionadoId = it },
            idVehicle = selectedVehicleId
        )
    }
}


@Composable
fun VehicleCard(
    vehicle: InfoVehicle,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUploadDocuments: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(
                        text = "Marca: ${vehicle.marca}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Modelo: ${vehicle.modeloVehiculo}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column {
                    Text(
                        text = "Placa: ${vehicle.placa}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Estado: ${if (vehicle.estadoVehiculo) "Activo" else "Inactivo"}",
                        style = MaterialTheme.typography.titleMedium
                    )

                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar Vehículo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Editar Vehículo")
                }
                Button(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar Vehículo")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar Vehículo")
                }

            }
            Button(onClick = onUploadDocuments) {
                Icon(Icons.Filled.CloudUpload, contentDescription = "Cargar Documentos")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cargar Documentos")
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadModal(
    onDismissRequest: () -> Unit,
    onUploadClick: (String, String, String, String) -> Unit,
    apiService: ApiService,
    tokenManager: TokenManager,
    onServicioSelected: (String?) -> Unit,
    idVehicle: String?
) {
    var selectedType by remember { mutableStateOf("Selecciona tipo de documento") }
    var selectedDate by remember { mutableStateOf("") }
    var numberInput by remember { mutableStateOf(TextFieldValue("")) }
    var pdfFilePath by remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()  // Use the coroutine scope

    Log.d("DocumentUploadModal", "ID del vehículo: $idVehicle")

    val repository = remember { TipoDocumentoRepository(apiService, tokenManager) }
    val responseTipoDctoVehicle by produceState<MyResponseTipoDctoVehicle?>(initialValue = null) {
        withContext(Dispatchers.IO) {
            value = repository.fecthTipoDocumento()
        }
    }

    val context = LocalContext.current

    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                pdfFilePath = it
                Toast.makeText(context, "Documento PDF seleccionado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val options = responseTipoDctoVehicle?.data?.tipoDocVehiculo
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options?.firstOrNull()) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Cargar Documento")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // ExposedDropdownMenu to select the service
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedOption?.nombre ?: "",
                        onValueChange = {},
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

                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (selectedDate.isEmpty()) "Selecciona fecha" else selectedDate)
                }

                OutlinedTextField(
                    value = numberInput,
                    onValueChange = { numberInput = it },
                    label = { Text("Número") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        openFileLauncher.launch("application/pdf")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cargar Documento PDF")
                }

                pdfFilePath?.let {
                    Text(
                        text = "Documento Seleccionado: ${it.lastPathSegment}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedOption != null && selectedDate.isNotEmpty() && numberInput.text.isNotEmpty() && pdfFilePath != null) {
                    onUploadClick(
                        selectedOption?.nombre ?: "",
                        selectedDate,
                        numberInput.text,
                        pdfFilePath!!.toString()
                    )

                    coroutineScope.launch {
                        SubmitDocumentation(
                            apiService = apiService,
                            tokenManager = tokenManager,
                            formData = FormDataDocumentation(
                                tipoDocumentoId = selectedOption?._id ?: "",
                                idVehiculo = idVehicle ?: "",
                                fechaExpiracion = selectedDate,
                                numeroDocumento = numberInput.text,
                                documento = pdfFilePath
                            ),
                            context = context,
                            onClose = { onDismissRequest() }
                        )
                    }

                } else {
                    Toast.makeText(
                        context,
                        "Por favor, completa todos los campos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Text("Subir Documento")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}


//@Preview
//@Composable
//fun PreviewDocumentUploadModal() {
//    var showModal by remember { mutableStateOf(true) }
//
//    if (showModal) {
//        DocumentUploadModal(
//            onDismissRequest = { showModal = false },
//            onUploadClick = { type, date, number, path ->
//                println("Tipo: $type, Fecha: $date, Número: $number, PDF: $path")
//                showModal = false
//            }
//        )
//    }
//}

data class FormDataDocumentation(
    val tipoDocumentoId: String,
    val idVehiculo: String,
    val fechaExpiracion: String,
    val numeroDocumento: String,
    val documento: Uri?
)


fun SubmitDocumentation(
    apiService: ApiService,
    tokenManager: TokenManager,
    formData: FormDataDocumentation,
    context: Context,
    onClose: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val isExist = ValidateDocumentType(
            apiService = apiService,
            tokenManager = tokenManager,
            idVehicle = formData.idVehiculo,
            idTipoDocSelected = formData.tipoDocumentoId,
            context = context
        )

        if (isExist) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "⚠️ El tipo de documento ya existe", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            try {
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

                Log.d("submitDocument", "el id del vehiculo es : ${formData.idVehiculo}")
                Log.d(
                    "submitDocument",
                    "el id del vehiculo es : $idVehiculo " +
                            "el id del tipo de documento es : $idTipoDoc " +
                            "la fecha de expiracion es : $fechaExpiracion " +
                            "el numero de documento es : $numeroDocumento " +
                            "el documento es : $documentPart"
                )

                val response = apiService.uploadDocumentVehicle(
                    tipoDocumentoId = idTipoDoc,
                    idVehiculo = idVehiculo,
                    fechaExpiracion = fechaExpiracion,
                    numeroDocumento = numeroDocumento,
                    documento = documentPart
                )

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        onClose()
                        Toast.makeText(context, "✅ Documento Cargado con éxito", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("submitDocument", "❌ Error: ${response.errorBody()?.string()}")
                        Toast.makeText(
                            context,
                            "❌ Error: ${response.errorBody()?.string()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("submitDocument", "❌ Error al subir documento: ${e.message}")
            }
        }
    }
}


fun copyUriToTempFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IOException("No se pudo abrir el URI: $uri")

    val tempFile = File(context.cacheDir, "temp_document.pdf")
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}


suspend fun ValidateDocumentType(
    apiService: ApiService,
    tokenManager: TokenManager,
    idVehicle: String,
    idTipoDocSelected: String,
    context: Context
): Boolean {
    return findIdTipoDocument(
        apiService = apiService,
        tokenManager = tokenManager,
        idVehicle = idVehicle,
        idTipoDocSelected = idTipoDocSelected,
        context = context
    )
}


suspend fun findIdTipoDocument(
    apiService: ApiService,
    tokenManager: TokenManager,
    idVehicle: String,
    idTipoDocSelected: String,
    context: Context
): Boolean {
    val repository = DocsVehiclesRepository(apiService, tokenManager)

    return try {
        val responseDocs = repository.fecthDocsData(idVehicle)
        val responseData = responseDocs?.data

        if (responseData.isNullOrEmpty()) {
            Log.d("findIdTipoDocument", "⚠️ No se encontraron documentos asociados al vehículo")
            return false
        }

        val idTipoDocList = responseData.mapNotNull { it.tipoDocumentoId._id }

        return if (idTipoDocList.contains(idTipoDocSelected)) {
            Log.d("findIdTipoDocument", "⚠️ El tipo de documento ya existe en el vehículo")
            true
        } else {
            Log.d("findIdTipoDocument", "✅ Tipo de documento disponible para registrar")
            false
        }

    } catch (e: Exception) {
        Log.e("findIdTipoDocument", "❌ Error al verificar el tipo de documento: ${e.message}")
        false
    }
}


@Preview(showBackground = true)
@Composable
fun GarajeScreenPreview() {
    GarajeScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        openDrawer = {},
        tokenManager = TokenManager(LocalContext.current)
    )
}
