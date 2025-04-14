package com.example.pesv_movil.desplazamientos

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    navController: NavHostController,
    desplazamientosViewModel: DesplazamientosViewModel
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
            desplazamientosViewModel.userLocation.value ?: LatLng(4.570868, -74.297333), 15f
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


    val context = LocalContext.current


    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()


    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, // Permite quedarse en estados intermedios
        confirmValueChange = { it != SheetValue.Hidden }, // ¡Evita que se oculte por completo!
    )


//    BottomSheetScaffold(
//        scaffoldState = rememberBottomSheetScaffoldState(
//            bottomSheetState = sheetState
//        ),
//        sheetContent = {
//            // Contenido del Bottom Sheet (ej: detalles de ubicación)
//            Box(modifier = Modifier.height(300.dp)) {
//                Text("Detalles del lugar", style = MaterialTheme.typography.headlineSmall)
//                Text("Arrástrame hacia arriba o abajo")
//            }
//        },
//        sheetPeekHeight = 64.dp, // Altura mínima visible cuando está "cerrado"
//    ) { paddingValues ->
//        // Contenido principal (ej: Mapa)
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            contentAlignment = Alignment.Center
//        ) {
//            MapView(
//                hasPermission = permissionState.value,
//                cameraPositionState = cameraPositionState,
//                locationLoading = locationLoading,
//                locationError = locationError,
//                onDismissError = { desplazamientosViewModel.locationError.value = null }
//            )
//        }
//    }


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 64.dp,


        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .background(Color.White)
//                    .clickable {
//                        scope.launch { scaffoldState.bottomSheetState.partialExpand() }
//
//                    }
            ) {

                Row(modifier = Modifier.padding(top = 5.dp)) {




                }


            }
        }


    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MapView(
                hasPermission = permissionState.value,
                cameraPositionState = cameraPositionState,
                locationLoading = locationLoading,
                locationError = locationError,
                onDismissError = { desplazamientosViewModel.locationError.value = null }
            )
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
    onDismissError: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasPermission)
        )

        if (locationLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (locationError != null) {
            ErrorDialog(message = locationError, onDismiss = onDismissError)
        }
    }
}


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