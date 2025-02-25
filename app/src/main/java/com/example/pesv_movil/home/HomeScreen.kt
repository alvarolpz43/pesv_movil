package com.example.pesv_movil.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
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

    }

}







@Preview(showBackground = true)
@Composable
fun BodyGarajePreview() {
}