package com.example.pesv_movil.preoperacional

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre
import com.example.pesv_movil.preoperacional.data.ResponseVehicleSinPre
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreoperacionalScreen(
    navController: NavController,
    onClose: () -> Unit,
    tokenManager: TokenManager
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)



    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Preoperacional") },
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

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {


                    Text(
                        text = "Vehiculos Pendientes de Preoperacional",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )


                    Box(modifier = Modifier.fillMaxSize()) {

                        GetVehiclesStatusInUse(
                            tokenManager = tokenManager,
                            apiService = apiService,
                            context = LocalContext.current,
                            nav = navController
                        )


                    }
                }

            }
        }
    )

}

@Composable
fun GetVehiclesStatusInUse(
    tokenManager: TokenManager,
    apiService: ApiService,
    context: Context,
    nav: NavController
) {
    var refreshUpdates by remember { mutableStateOf(0) }
    val vehiclesResponse by produceState<ResponseVehicleSinPre?>(
        initialValue = null,
        key1 = refreshUpdates
    ) {
        withContext(Dispatchers.IO) {
            try {
                val tokenValue = tokenManager.token.first() ?: ""
                val userId = tokenManager.getUserIdBlocking() ?: ""
                Log.d("FetchMyVehiculosId", "ID del usuario: $userId")

                val call = apiService.getVehicleSinPre("Bearer $tokenValue")
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

    val vehiclesInUse = vehiclesResponse?.data?.filter { it.vehiculoEnUso }

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
                Text("No hay vehículos Para Formulario.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!vehiclesInUse.isNullOrEmpty()) {
                    item {
                        Text(
                            text = "Vehículos en uso",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(vehiclesInUse) { vehicle ->
                        CardVehicleUsing(
                            vehicle = vehicle,
                            message = "No hay Vehículos en uso",
                            onClickListener = {
                                nav.navigate("form_preoperacional/${vehicle._id}")
                            }
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
            }

        }
    }


}

@Composable
fun CardVehicleUsing(
    vehicle: DataVehicleSinPre?,
    message: String,
    onClickListener: () -> Unit
) {

    if (vehicle != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClickListener() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    Column {

                        VehiculeIcon()

                    }
                    Column {
                        Text(
                            text = "Marca: ${vehicle.marca}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Modelo: ${vehicle.modeloVehiculo}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "En Uso: ${if (vehicle.vehiculoEnUso) "Si" else "No"}",
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


            }
        }
    } else {
        Text(text = message)
    }

}