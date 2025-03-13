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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.pesv_movil.Garaje.data.InfoVehicle
import com.example.pesv_movil.Garaje.data.MyResponseVehiculo
import com.example.pesv_movil.Garaje.data.TipoVehiculo
import com.example.pesv_movil.Garaje.data.UserInfo
import com.example.pesv_movil.Garaje.data.Zona
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.R
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.time.format.DateTimeFormatter
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

                FetchMyVehiculos(
                    tokenManager = tokenManager,
                    apiService = apiService,
                    context = LocalContext.current
                )


            }
        }
    }
}

@Composable
fun FetchMyVehiculos(tokenManager: TokenManager, apiService: ApiService, context: Context) {
    var refreshUpdates by remember { mutableStateOf(0) }
    val vehiclesResponse by produceState<MyResponseVehiculo?>(
        initialValue = null,
        key1 = refreshUpdates
    ) {
        value = try {
            withContext(Dispatchers.IO) {
                val tokenValue = tokenManager.token.firstOrNull() ?: ""
                val userId = tokenManager.getUserIdBlocking() ?: ""
                Log.d("FetchMyVehiculosId", "ID del usuario: $userId")

                val call = apiService.getMyVehiculos("Bearer $tokenValue")
                val response = call.execute()
                if (response.isSuccessful) {
                    Log.d("FetchMyVehiculos", "Respuesta exitosa: ${response.body()}")
                    response.body()
                } else {
                    Log.e("FetchMyVehiculos", "Error en la respuesta: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FetchMyVehiculos", "Error fetching vehicles: ${e.message}")
            null
        }
    }

    val vehiclesInUse = vehiclesResponse?.data?.filter { it.vehiculoEnUso } ?: emptyList()
    val vehiclesAvailable = vehiclesResponse?.data?.filter { !it.vehiculoEnUso } ?: emptyList()

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
        if (vehiclesResponse?.data?.isEmpty() == true) {
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
                                UpdateStatusVehicleInUsing(
                                    apiService = apiService,
                                    idVehicle = vehicle._id,
                                    context = context,
                                    tokenManager = tokenManager
                                ) {
                                    refreshUpdates++
                                }
                            },
                            onUploadDocuments = {},
                            message = "No hay Vehículos en uso"
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "No hay vehículos en uso",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (vehiclesAvailable.isNotEmpty()) {
                    item {
                        Text(
                            text = "Vehículos disponibles",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(vehiclesAvailable) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            onEdit = {},
                            onChangeStatus = {
                                UpdateStatusVehicleInUsing(
                                    apiService = apiService,
                                    idVehicle = vehicle._id,
                                    context = context,
                                    tokenManager = tokenManager
                                ) {
                                    refreshUpdates++
                                }
                            },
                            onDelete = {},
                            onUploadDocuments = {
                                selectedVehicleId = vehicle._id
                                showDocumentUploadModal = true
                            },
                            message = "No hay Vehículos Registrados"
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "No hay vehículos disponibles",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
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
    vehicle: InfoVehicle?,
    onEdit: () -> Unit,
    onChangeStatus: () -> Unit,
    onDelete: () -> Unit,
    onUploadDocuments: () -> Unit,
    message: String
) {
    if (vehicle != null) {
        Card(
            modifier = Modifier

                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {

            ConstraintLayout(
                modifier = Modifier
                    .padding(5.dp)
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
                        modifier = Modifier.fillMaxWidth(),
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

                        }

                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_garage),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp), // Tamaño del icono

                    )
                    Text(


                        text = " ${vehicle.placa}",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis


                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 3.dp),
                        text = " ${vehicle.fechaMatricula}",
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

                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Cambiar estado")
                        if (LocalConfiguration.current.screenWidthDp > 360) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar")
                        }
                    }

                    Button(
                        onClick = onUploadDocuments,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
                        enabled = !vehicle.vehiculoEnUso,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
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


fun UpdateStatusVehicleInUsing(
    apiService: ApiService,
    idVehicle: String,
    context: Context,
    tokenManager: TokenManager,
    onSuccess: () -> Unit
) {

    CoroutineScope(Dispatchers.IO).launch {
        try {

            val token = tokenManager.token.first() ?: ""

            Log.d("UpdateStatusVehicleInUsing", "el token es : $token")

            val response = apiService.updateVehicleStateUsing("Bearer $token", idVehicle)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onSuccess()
                    Toast.makeText(
                        context,
                        "✅ El vehículo ha sido actualizado con éxito",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(
                        context,
                        "Error al actualizar el estado del vechiculo: $errorBody",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(
                        "UpdateStatusVehicleInUsing",
                        "Error al actualizar el estado del vehículo: $errorBody"
                    )
                }
            }

        } catch (e: Exception) {
            Log.e(
                "UpdateStatusVehicleInUsing",
                "Error al actualizar el estado del vehículo: ${e.message}"
            )
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
    val coroutineScope = rememberCoroutineScope()

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
                            onClose = {
                                onDismissRequest()
                            }
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
                        Toast.makeText(context, "✅ Documento Cargado con éxito", Toast.LENGTH_SHORT)
                            .show()
                        onClose()
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
fun VehicleCardPreview() {
    VehicleCard(
        vehicle = InfoVehicle(
            "id",
            UserInfo("user", "user"),
            UserInfo("user", "user"),
            "sfgsggdffg",
            TipoVehiculo("sdfsdfd", "fsdfd", "El Vehiculo"),
            Zona("zona", "Norte", "1"),
            "marca",
            "service",
            2,
            2024,
            "verde",
            "fecha",
            "NEL02C",
            true,
            true,
            true,
            "fechaCracion",
            "fechas",
            "feachas", 2
        ),
        onEdit = {},
        onChangeStatus = {},
        onDelete = {},
        onUploadDocuments = {},
        message = "No hay Vehículos Registrados"
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GarajeScreenPreview() {
//    GarajeScreen(
//        navController = androidx.navigation.compose.rememberNavController(),
//        openDrawer = {},
//        tokenManager = TokenManager(LocalContext.current)
//    )
//}
