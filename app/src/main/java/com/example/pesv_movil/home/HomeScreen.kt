package com.example.pesv_movil.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.example.pesv_movil.PesvScreens
import com.example.pesv_movil.desplazamientos.DesplazamientosIcon
import com.example.pesv_movil.desplazamientos.GreenLogo
import com.example.pesv_movil.desplazamientos.PreoperacionalIcon
import com.example.pesv_movil.utils.HomeTopAppBar
import com.example.pesv_movil.utils.TokenManager


@Composable
fun HomeScreen(
    navController: NavHostController,
    tokenManager: TokenManager,
    openDrawer: () -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { HomeTopAppBar(openDrawer = openDrawer) }
    ) { paddingValues ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (buttonDesplazamientos, buttonPreoperacional, logo) = createRefs()
//            val topGuide = createGuidelineFromTop(0.1f)

            GreenLogo(modifier = Modifier
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
                    .constrainAs(buttonPreoperacional) {
                        top.linkTo(logo.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable { println("Preoperacional clicado") },
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
                    .clickable { navController.navigate(PesvScreens.DESPLAZAMIENTOS_SCREEN) },
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
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



            GreenLogo(modifier = Modifier
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
                    .constrainAs(buttonPreoperacional) {
                        top.linkTo(logo.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable { println("Preoperacional clicado") },
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
                    }
                    .clickable { println("Desplazamientos clicado") },
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
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