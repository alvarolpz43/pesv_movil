package com.example.pesv_movil.desplazamientos

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pesv_movil.PesvScreens
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    navController: NavHostController,
    desplazamientosViewModel: DesplazamientosViewModel,
    origenSeleccionado: LatLng,   // ✅ Ubicación de origen
    destinoSeleccionado: LatLng
) {
    val permissionState = remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = isGranted
        if (isGranted) desplazamientosViewModel.getCurrentLocation()
    }

    LaunchedEffect(Unit) {
        if (!permissionState.value) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            desplazamientosViewModel.getCurrentLocation()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            desplazamientosViewModel.userLocation.value ?: LatLng(
                4.570868,
                -74.297333
            ), 15f
        )
    }

    LaunchedEffect(desplazamientosViewModel.userLocation.value) {
        desplazamientosViewModel.userLocation.value?.let { latLng ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    }

    val showModal = desplazamientosViewModel.showModal.value
    val locationLoading = desplazamientosViewModel.locationLoading.value
    val locationError = desplazamientosViewModel.locationError.value

    BottomSheetScaffold(
        sheetPeekHeight = 150.dp,
        sheetSwipeEnabled = false,
        containerColor = Color.White,
        sheetContent = { BottomSheetContent { navController.navigate(PesvScreens.BUSCAR_UBICACION_SCREEN)}}
    ) {
        MapView(permissionState.value, cameraPositionState, locationLoading, locationError, destinoSeleccionado) {
            desplazamientosViewModel.locationError.value = null
        }
    }

    if (showModal) {
        DestinationModal { desplazamientosViewModel.showModal.value = false }
    }
}


@Composable
fun MapView(
    hasPermission: Boolean,
    cameraPositionState: CameraPositionState,
    locationLoading: Boolean,
    locationError: String?,
    destinoSeleccionado: LatLng,
    onDismissError: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasPermission)
        ) {
            Marker(
                state = MarkerState(position = destinoSeleccionado),
                title = "Ubicación seleccionada"
            )
        }

        if (locationLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (locationError != null) {
            ErrorDialog(message = locationError, onDismiss = onDismissError)
        }
    }
}


@Composable
fun BottomSheetContent(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()  // ✅ Ajusta la altura dinámicamente en lugar de 900.dp
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable { onClick() }
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Ingresa tu destino", color = Color.Gray, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                modifier = Modifier,
                textAlign = TextAlign.Center,
                text = "Error",
                fontSize = 20.sp
            )
        },
        text = { Text(text = message, fontSize = 16.sp, textAlign = TextAlign.Center) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Aceptar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinationModal(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Contenido del Modal")
        }
    }
}
