package com.example.pesv_movil.desplazamientos.model

data class VehiculoResponse(
    val _id: String,
    val marca: String
)


data class OpcionVehiculo(
    val id: String, // El ID que quieres enviar al backend
    val displayText: String // Lo que se muestra en la UI
)

