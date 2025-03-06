package com.example.pesv_movil.components

data class MyResponseTipoDctoVehicle(
    val success: Boolean,
    val data: ArrayResponse
)


data class ArrayResponse(
    val tipoDocVehiculo: List<DataTipoDctoVehicle>,
    val tipoDocUsuario: List<DataTipoDctoUsuario>
)

data class DataTipoDctoVehicle(
    val _id: String,
    val nombre: String,
    val categoria: String,
    val descripcion: String
)

data class DataTipoDctoUsuario(
    val _id: String,
    val nombre: String,
    val categoria: String,
    val descripcion: String
)
