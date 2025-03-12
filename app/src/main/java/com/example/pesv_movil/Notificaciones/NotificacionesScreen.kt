package com.example.pesv_movil.Notificaciones

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.pesv_movil.Notificaciones.data.DataNotifications
import com.example.pesv_movil.Notificaciones.data.MyResponseNotifications
import com.example.pesv_movil.R
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.RoundedNotify
import com.example.pesv_movil.utils.NotifyAppBar
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


@Composable
fun NotificacionesScreen(
    navController: NavController,
    tokenManager: TokenManager,
    openDrawer: () -> Unit
) {
    val context = LocalContext.current
    val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { NotifyAppBar(openDrawer = openDrawer) }
    ) { paddingValues ->

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (notis) = createRefs()
//            val topGuide = createGuidelineFromTop(0.1f)


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .constrainAs(notis) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                GetNotifications(
                    tokenManager,
                    apiService
                )
            }

        }


    }

}

@Composable
fun GetNotifications(tokenManager: TokenManager, apiService: ApiService) {
    val notiResponse by produceState<MyResponseNotifications?>(
        initialValue = null,
    ) {
        value = try {
            withContext(Dispatchers.IO) {
                val tokenValue = tokenManager.token.firstOrNull() ?: ""

                val call = apiService.getMyNotificaciones("Bearer $tokenValue")
                val response = call.execute()
                if (response.isSuccessful) {
                    Log.d("FetchNotifications", "Respuesta exitosa: ${response.body()}")
                    response.body()
                } else {
                    Log.e("FetchNotifications", "Error en la respuesta: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FetchNotifications", "Error fetching notifications: ${e.message}")
            null
        }
    }

    val notifications = notiResponse?.data?.filter { !it.leida } ?: emptyList()

    if (notiResponse == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        if (notiResponse?.data?.isEmpty() == true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay notificaciones.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (notifications.isNotEmpty()) {
                    items(notifications) { notis ->
                        NotificationCard(notis = notis)
                    }
                } else {
                    item {
                        Text(
                            text = "No hay Notificaciones",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun NotificationCard(
    notis: DataNotifications?
) {
    if (notis != null) {
        val timeAgo = calculateTimeAgo(notis.fechaNotificacion)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 3.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Mejor distribución en todas las pantallas
            ) {
                // Icono de notificación
                RoundedNotify(modifier = Modifier.size(48.dp)) // Ajusta el tamaño del icono según la pantalla

                Spacer(modifier = Modifier.width(12.dp)) // Espacio entre el icono y el texto

                // Contenido de la notificación
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp) // Espacio para evitar que el texto toque los bordes
                ) {
                    Text(

                        text = " ${notis.tipoNotificacion}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = notis.detalle,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Espacio flexible antes del tiempo
                Spacer(modifier = Modifier.width(8.dp))

                // Tiempo de la notificación
                Text(
                    text = "Hace $timeAgo",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.wrapContentWidth(Alignment.End) // Evita que se corte en pantallas pequeñas
                )
            }
        }


    } else {
        Text(text = "No hay notificaciones")
    }
}


fun calculateTimeAgo(fecha: String): String {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        val fechaNotificacion = formatter.parse(fecha)


        val ahora = Date()

        val diffMillis = ahora.time - fechaNotificacion.time

        when {
            TimeUnit.MILLISECONDS.toMinutes(diffMillis) < 60 ->
                "${TimeUnit.MILLISECONDS.toMinutes(diffMillis)} minutos"

            TimeUnit.MILLISECONDS.toHours(diffMillis) < 24 ->
                "${TimeUnit.MILLISECONDS.toHours(diffMillis)} horas"

            TimeUnit.MILLISECONDS.toDays(diffMillis) < 30 ->
                "${TimeUnit.MILLISECONDS.toDays(diffMillis)} días"

            else ->
                "Más de un mes"
        }
    } catch (e: Exception) {
        "Fecha inválida"
    }
}


@Preview(showBackground = true)
@Composable
fun CardNotyPreview() {
    NotificationCard(
        notis = DataNotifications("fsdfsdf", "fsdfsd", "sdfsdf", "fsdfsdf", false, "fssdfsd")
    )
}