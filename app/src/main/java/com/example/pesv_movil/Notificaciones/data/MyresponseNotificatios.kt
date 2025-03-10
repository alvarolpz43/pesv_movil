package com.example.pesv_movil.Notificaciones.data

data class MyResponseNotifications(
    val success: Boolean,
    val data: List<DataNotifications>,
)

data class DataNotifications(
    val _id: String,
    val idUsuario: String,
    val tipoNotificacion: String,
    val detalle: String,
    val leida: Boolean,
    val fechaNotificacion: String
)