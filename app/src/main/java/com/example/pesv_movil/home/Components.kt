package com.example.pesv_movil.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pesv_movil.R


@Composable
fun CheckIcon(modifier: Modifier = Modifier.size(50.dp)) {
    Image(
        painter = painterResource(id = R.drawable.ic_check),
        contentDescription = "Ícono de Desplazamientos",
        contentScale = ContentScale.Fit, // Ajusta sin deformar
        modifier = modifier
    )
}

@Composable
fun ClockIcon(modifier: Modifier = Modifier.size(50.dp)) {
    Image(
        painter = painterResource(id = R.drawable.ic_clock),
        contentDescription = "Ícono de Desplazamientos",
        contentScale = ContentScale.Fit, // Ajusta sin deformar
        modifier = modifier
    )
}