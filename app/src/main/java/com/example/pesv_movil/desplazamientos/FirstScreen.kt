package com.example.pesv_movil.desplazamientos


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreenDesplazamientos(
    navController: NavHostController,
    desplazamientosViewModel: DesplazamientosViewModel
) {


    val opciones by desplazamientosViewModel.opciones.collectAsState()



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 56.dp)
                    ) {
                        Text(
                            text = "Desplazamientos",
                            modifier = Modifier.align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {

                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Button(
                    modifier = Modifier.padding(5.dp),
                    onClick = { desplazamientosViewModel.enviarFormulario(navController) }
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Iniciar Desplazamiento",
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 10.dp,
                    bottom = paddingValues.calculateBottomPadding() + 10.dp,
                    start = 10.dp,
                    end = 10.dp
                )

                .verticalScroll(rememberScrollState()),

            verticalArrangement = Arrangement.spacedBy(12.dp)  //


        ) {
            SelectField(
                modifier = Modifier,
                label = "Vehiculo",
                options = opciones,
                selectedOption = desplazamientosViewModel.vehiculoSeleccionado,
                onOptionSelected = desplazamientosViewModel::onVehiculoSeleccionado,
                error = desplazamientosViewModel.vehiculoSeleccionadoError
            )



            InputText(
                value = desplazamientosViewModel.p_inicio,
                onValueChange = desplazamientosViewModel::onChangePuntoInicio,
                label = "Punto de Inicio",
                error = desplazamientosViewModel.p_inicioError
            )



            InputText(
                value = desplazamientosViewModel.p_final,
                onValueChange = desplazamientosViewModel::onChangePuntoFinal,
                label = "Punto de Final",
                error = desplazamientosViewModel.p_FinalError
            )


        }


    }


}

