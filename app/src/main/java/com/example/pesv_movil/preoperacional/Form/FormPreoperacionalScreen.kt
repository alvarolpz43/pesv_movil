package com.example.pesv_movil.preoperacional.Form

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pesv_movil.preoperacional.ListaVehiculos.VehicleCard
import com.example.pesv_movil.preoperacional.data.DataPregunta
import com.example.pesv_movil.preoperacional.data.DataVehicleSinPre


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPreoperacionalScreen(
    navController: NavController,
    viewModel: FormPreoperacionalViewModel,
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val formulario by viewModel.formulario.collectAsState()
    val context = LocalContext.current

    var preguntaActual by remember { mutableStateOf(0) }
    val totalPreguntas = formulario?.preguntas?.size ?: 0
    var todasRespondidas by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    val sendSuccess by viewModel.sendSuccess.collectAsState()









    // Verificar si todas las preguntas están respondidas
    LaunchedEffect(viewModel.respuestas) {
        todasRespondidas = viewModel.respuestas.values.all { it != null }
    }



    // Observar si el formulario se envió correctamente
    LaunchedEffect(sendSuccess) {
        if (sendSuccess == true) { // Compara explícitamente con true
            showSuccessDialog = true
        }
    }

    SuccessModal(
        showDialog = showSuccessDialog,
        onDismiss = {
            showSuccessDialog = false
            navController.popBackStack() // Ahora se cierra correctamente tras la confirmación
        }
    )







    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formulario Preoperacional") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver")
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
                formulario != null && totalPreguntas > 0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Barra de progreso con indicador numérico
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso: ${preguntaActual + 1}/$totalPreguntas",
                                fontSize = 16.sp
                            )
                            LinearProgressIndicator(
                                progress = (preguntaActual + 1) / totalPreguntas.toFloat(),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Pregunta actual
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            formulario?.preguntas?.get(preguntaActual)?.let { pregunta ->
                                PreguntaItem(
                                    pregunta = pregunta,
                                    respuestaActual = viewModel.respuestas[pregunta._id],
                                    onRespuestaSeleccionada = { respuesta ->
                                        viewModel.respuestas[pregunta._id] = respuesta
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Botones de navegación
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Botón "Atrás"
                            if (preguntaActual > 0) {
                                Button(
                                    onClick = { preguntaActual-- },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Atrás")
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            // Botón "Siguiente" o "Enviar"
                            if (preguntaActual < totalPreguntas - 1) {
                                Button(
                                    onClick = { preguntaActual++ },
                                    enabled = viewModel.respuestas[formulario!!.preguntas[preguntaActual]._id] != null
                                ) {
                                    Text("Siguiente")
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.enviarRespuestas(context)

                                    },
                                    enabled = todasRespondidas,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                                ) {
                                    Text("Enviar formulario")
                                }
                            }
                        }
                    }
                }
                else -> {
                    Text(
                        text = "No hay formularios pendientes por completar.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PreguntaItem(
    pregunta: DataPregunta,
    respuestaActual: Boolean?,
    onRespuestaSeleccionada: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pregunta
        Text(
            text = pregunta.preguntaTexto,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        // Opciones de respuesta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Opción positiva
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = respuestaActual == true,
                    onCheckedChange = {
                        if (it) {
                            onRespuestaSeleccionada(true)
                        } else if (respuestaActual == true) {
                            // Permite deseleccionar
                            onRespuestaSeleccionada(false)
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "En buen estado",
                    color = Color.Green,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Opción negativa
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = respuestaActual == false,
                    onCheckedChange = {
                        if (it) {
                            onRespuestaSeleccionada(false)
                        } else if (respuestaActual == false) {
                            // Permite deseleccionar
                            onRespuestaSeleccionada(true)
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "En mal estado",
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }


}




@Composable
fun SuccessModal(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = Color(0xFF4CAF50), // Verde éxito
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Éxito!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "El formulario se registró correctamente.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Aceptar", color = Color.White)
                    }
                }
            }
        )
    } else {
        // Mensaje alternativo cuando showDialog es falso
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Información",
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No hay notificaciones en este momento.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SuccessModalPreview() {
    SuccessModal(
        showDialog = true,
        onDismiss = {}
    )
}