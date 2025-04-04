package com.example.pesv_movil.preoperacional.ListaVehiculos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.pesv_movil.R
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreoperacionalScreen(
    preoperacionalViewModel: PreoperacionalViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val vehicles by preoperacionalViewModel.vehicles.collectAsState()
    val isLoading by preoperacionalViewModel.isLoading.collectAsState()
    val showDialog by preoperacionalViewModel.showDialog.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Vehículos") },
                navigationIcon = {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                vehicles.isEmpty() -> {
                    Text(
                        text = "Estás al día, no hay vehículos pendientes.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(vehicles) { vehicle ->
                            VehicleCard(
                                vehicle = vehicle,
                                onClick = { preoperacionalViewModel.onVehicleSelected(vehicle._id) }
                            )
                        }
                    }
                }
            }


            if (showDialog) {
                AlertDialogForm(
                    onDismiss = preoperacionalViewModel::onDismissDialog,
                    onConfirm = {
                        preoperacionalViewModel.onPerformPreoperacional()
                        preoperacionalViewModel.selectedVehicleId?.let { id ->
                            navController.navigate("form_preoperacional/$id")
                        }
                    },
                    onDismissButton = {
                        preoperacionalViewModel.selectedVehicleId?.let { id ->
                            preoperacionalViewModel.sendPreoperacionalNoAplica(id)
                        }
                        preoperacionalViewModel.onDismissDialog()
                    }
                )
            }


        }
    }


    // ... tus otros estados existentes ...
    var showNoAplicaSuccess by remember { mutableStateOf(false) }


    // Diálogo de confirmación de "No Aplica"
    if (showNoAplicaSuccess) {
        AlertDialog(
            onDismissRequest = { preoperacionalViewModel.resetNoAplicaSuccess() },
            title = { Text("Confirmación") },
            text = { Text("Preoperacional registrado como no aplica") },
            confirmButton = {
                Button(
                    onClick = { preoperacionalViewModel.resetNoAplicaSuccess() }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Modal de confirmación (nuevo)
    if (showNoAplicaSuccess) {
        AlertDialog(
            onDismissRequest = { showNoAplicaSuccess = false },
            title = { Text("Confirmación") },
            text = { Text("Preoperacional registrado como no aplica") },
            confirmButton = {
                Button(onClick = { showNoAplicaSuccess = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}


@Composable
fun VehicleCard(vehicle: DataVehicleSinPre, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
            .border(
                1.dp,
                colorResource(id = R.color.black),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        ConstraintLayout(modifier = Modifier.padding(10.dp)) {
            val (title, subTitle) = createRefs()

            Text(
                text = "${vehicle.marca} ${vehicle.modeloVehiculo}",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            Row(
                modifier = Modifier
                    .padding(3.dp)
                    .constrainAs(subTitle) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        .background(Color(0xFFFFEB3B), RoundedCornerShape(8.dp))
                        .border(1.dp, colorResource(id = R.color.black), RoundedCornerShape(8.dp))
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
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Color: ${vehicle.color}",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "Capacidad: ${vehicle.capacidadVehiculo}",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}


@Composable
fun AlertDialogForm(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDismissButton: () -> Unit // Cambia esto para que sea una función lambda
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Acción Preoperacional",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Seleccione una opción para este vehículo:",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Realizar Preoperacional", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissButton() // Ejecuta la función directamente
                    onDismiss() // Cierra el diálogo
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Marcar como No aplica", color = Color.White)
            }
        }
    )
}
