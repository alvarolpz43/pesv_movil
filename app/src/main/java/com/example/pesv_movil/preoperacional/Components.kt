package com.example.pesv_movil.preoperacional

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pesv_movil.R


@Composable
fun RoundedNotify(modifier: Modifier = Modifier.size(50.dp)) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(Color.LightGray) // Fondo sutil para el icono
            .border(2.dp, Color.Gray, CircleShape) // Borde opcional
    ) {
        Image(
            painter = painterResource(id = R.drawable.id_notify),
            contentDescription = "Ícono de Notify",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(32.dp) // Tamaño del icono dentro del círculo
                .padding(8.dp)
        )
    }
}

