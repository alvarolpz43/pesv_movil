package com.example.pesv_movil.preoperacional.ListaVehiculos

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val vehicles by preoperacionalViewModel.vehicles.collectAsState()
    val isLoading by preoperacionalViewModel.isLoading.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current


    // Navegación manejada aqui porque
    // Composable: Debe manejar la UI y navegación (cosas específicas de Android).
    fun goBack() {
        navController.popBackStack()
        keyboardController?.hide()
    }

    fun goToForm(id_vehiculo: String) {
        Log.i("id_vehicle", id_vehiculo)
        navController.navigate("form_preoperacional/${id_vehiculo}")
    }

    val showDialog by preoperacionalViewModel.showDialog.collectAsState()

    if (showDialog) {
        AlertDialogFormPreviewable(
            onDismiss = { preoperacionalViewModel.onDismissDialog() },
            onConfirm = {
                preoperacionalViewModel.onPerformPreoperacional()
                navController.navigate("form_preoperacional/${preoperacionalViewModel.selectedVehicleId}")
            },
            onDismissButton = {
                // Acción para enviar vacío si aplica
            }
        )

    }


    //Es el header
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Vehículos") },
                navigationIcon = {
                    IconButton(onClick = {
                        goBack()

                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }

            )

        },
        /*floatingActionButton = {
            FloatingActionButton(onClick = { */
        /* Acción del botón flotante */
        /* }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }*/
        //Ese el boton flotane
    ) { paddingValues -> //El contenido despues del header
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (!isLoading && vehicles.isEmpty()) {
                    Text(
                        text = "Estás al día, no hay vehículos pendientes.",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(vehicles) { vehicle ->
                            VehicleCard(
                                vehicle,
                                onClick = { preoperacionalViewModel.onVehicleSelected(vehicle._id) }
                            )
                        }
                    }
                }
            }

        }
    }

}

//Preview
@Preview(showBackground = true)
@Composable
fun VehicleCardPreview() {
    VehicleCard(
        vehicle = DataVehicleSinPre(
            _id = "VH001",
            idUsuario = "USR001",
            idUsuarioAsignado = "USR002",
            idClaseVehiculo = "SUV",
            idTipoVehiculo = "AUTO",
            idZona = "NORTE",
            marca = "Toyota",
            servicio = "Particular",
            capacidadVehiculo = 5,
            modeloVehiculo = 2023,
            color = "Blanco",
            fechaMatricula = "2023-01-15",
            placa = "ABC123",
            VehicleEmpresa = false,
            vehiculoEnUso = true,
            estadoVehiculo = true,
            fechaCreacion = "2023-01-10"
        ),
        onClick = {}
    )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

        ) {

        ConstraintLayout(
            modifier = Modifier.padding(10.dp)
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

                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Capacidad: ${vehicle.capacidadVehiculo}",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )


                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDialogFormPreview() {
    AlertDialogFormPreviewable(
        onDismiss = {},
        onConfirm = {},
        onDismissButton = {}
    )
}



@Composable
fun AlertDialogFormPreviewable(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDismissButton: () -> Unit
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Realizar Preoperacional", color = Color.White)
                }
            }
        },
        dismissButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onDismissButton,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Marcar como No aplica", color = Color.White)
                }
            }
        }
    )
}



