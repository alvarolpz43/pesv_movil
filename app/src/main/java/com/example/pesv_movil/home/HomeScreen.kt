package com.example.pesv_movil.home

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.R
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.desplazamientos.DesplazamientosIcon
import com.example.pesv_movil.desplazamientos.GreenLogo
import com.example.pesv_movil.desplazamientos.PreoperacionalIcon
import com.example.pesv_movil.preoperacional.data.ResponseVehicleSinPre
import com.example.pesv_movil.utils.HomeTopAppBar
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


@Composable
fun HomeScreen(
    navController: NavHostController,
    tokenManager: TokenManager,
    openDrawer: () -> Unit
) {
    val context = LocalContext.current
    val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { HomeTopAppBar(openDrawer = openDrawer) }
    ) { paddingValues ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (buttonDesplazamientos, buttonPreoperacional, logo, notify) = createRefs()
//            val topGuide = createGuidelineFromTop(0.1f)


            GreenLogo(
                modifier = Modifier
                    .size(150.dp)
                    .constrainAs(logo)
                    {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    })

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(120.dp)
                    .constrainAs(notify) {
                        top.linkTo(logo.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }



            ) {
                NotifyPreoperacional(
                    tokenManager = tokenManager,
                    apiService = apiService,
                    context = context,
                    nav = navController
                )
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(120.dp)
                    .constrainAs(buttonPreoperacional) {
                        top.linkTo(notify.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable { navController.navigate(PesvScreens.PREOPE_SCREEN) },
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), // Espaciado interno
                    verticalAlignment = Alignment.CenterVertically // Centrar ícono y texto verticalmente
                ) {


                    PreoperacionalIcon(
                        modifier = Modifier
                    )
                    Text(
                        text = "Pre-Operacional",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp) // Espaciado entre ícono y texto
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(120.dp)
                    .constrainAs(buttonDesplazamientos) {
                        top.linkTo(buttonPreoperacional.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable { navController.navigate(PesvScreens.DESPLAZAMIENTOS_SCREEN) }, //Aqui voy al MapaAcreen
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), // Espaciado interno
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    DesplazamientosIcon(
                        modifier = Modifier

                    )
                    Text(
                        text = "Desplazamientos",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }


    }
}

@Composable
fun NotifyPreoperacional(
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

                val response = apiService.getVehicleSinPreoperacional("Bearer $tokenValue")
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .wrapContentSize(Alignment.Center), // Centra sin ocupar toda la pantalla
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.9f), // Ajusta el ancho en pantallas grandes
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CheckIcon() // Ícono de verificación

                    Spacer(modifier = Modifier.height(16.dp)) // Espacio entre ícono y texto

                    Text(
                        text = "Estás al día",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth(0.8f) // Evita que el texto se expanda demasiado en pantallas grandes
                            .padding(horizontal = 16.dp)
                    )
                }
            }


        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Padding general
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(), // Usa todo el ancho disponible
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ClockIcon() // Ícono del reloj

                    Spacer(modifier = Modifier.height(16.dp)) // Espacio entre ícono y texto

                    Text(
                        text = "Completa tu preoperacional",
                        fontSize = 18.sp, // Reducido para evitar desbordes
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier
                            .padding(horizontal = 16.dp) // Asegura espacio lateral
                            .fillMaxWidth(), // Permite que el texto use el ancho necesario
                        style = TextStyle(lineHeight = 22.sp) // Espaciado adecuado entre líneas
                    )
                }
            }


        }
    }


}

@Preview(showBackground = true)
@Composable
fun BodyGarajePreview() {
    HomeScreenPreviewOnlyUI();
}


// 🔹 Versión sin navController ni TokenManager solo para preview
@Composable
fun HomeScreenPreviewOnlyUI() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { Text(text = "Vista de prueba", modifier = Modifier.padding(16.dp)) }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (buttonDesplazamientos, buttonPreoperacional, logo) = createRefs()



            GreenLogo(
                modifier = Modifier
                    .size(150.dp)
                    .constrainAs(logo)
                    {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    })



            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(120.dp)
                    .border(
                        1.dp,
                        colorResource(id = R.color.black),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .constrainAs(buttonPreoperacional) {
                        top.linkTo(logo.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable { println("Preoperacional clicado") },
                colors = CardDefaults.cardColors(containerColor = Color.White),

                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), // Espaciado interno
                    verticalAlignment = Alignment.CenterVertically // Centrar ícono y texto verticalmente
                ) {


                    PreoperacionalIcon(
                        modifier = Modifier
                    )
                    Text(
                        text = "Pre-Operacional",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp) // Espaciado entre ícono y texto
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(120.dp)

                    .constrainAs(buttonDesplazamientos) {
                        top.linkTo(buttonPreoperacional.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), // Espaciado interno
                    verticalAlignment = Alignment.CenterVertically // Centrar ícono y texto verticalmente
                ) {


                    DesplazamientosIcon(
                        modifier = Modifier

                    )
                    Text(
                        text = "Desplazamientos",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp) // Espaciado entre ícono y texto
                    )
                }
            }

        }
    }
}