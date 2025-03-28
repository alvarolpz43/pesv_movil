package com.example.pesv_movil.preoperacional

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreoperacionalScreen2(
    preoperacionalViewModel: PreoperacionalViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) } // Estado de carga
    val data = remember { mutableStateOf<String?>(null) }

    val vehicles = remember { mutableStateOf<List<DataVehicleSinPre>>(emptyList()) }


    // Efecto hace la llamada de los vehiculosSinPreoperacional
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = preoperacionalViewModel.getCall()
            if (result != null) {
                vehicles.value = result
            }

            isLoading = false // Finaliza la carga


        }
    }


    //Es el header
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lista de Vehículos") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Acción del botón flotante */ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
            }
        }
    ) { paddingValues -> //El contenido despues del header
        Box(
            modifier = Modifier
                .fillMaxSize() // Ocupar todo el espacio disponible
                .padding(paddingValues),
            contentAlignment = Alignment.Center // Centrar el contenido dentro del Box
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn( //Lazi es la lista
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(vehicles.value) { vehicle -> // Por cada item se usa el compose VehicleCard
                        VehicleCard(
                            vehicle,
                            onClick = {
                                preoperacionalViewModel.goToForm(vehicle._id)
                            }
                        )
                    }
                }
            }
        }
    }

}


@Composable
fun VehicleCard(vehicle: DataVehicleSinPre, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "ID: ${vehicle._id}", fontWeight = FontWeight.Bold)
            Text(text = "Placa: ${vehicle.placa}")
            Text(text = "Marca: ${vehicle.marca}")

        }
    }
}