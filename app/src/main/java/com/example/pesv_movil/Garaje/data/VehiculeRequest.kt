package com.example.pesv_movil.Garaje.data

data class VehiculeRequest(
    val idClaseVehiculo: String,
    val idTipoVehiculo: String,
    val idZona: String,
    val marca: String,
    val servicio: String,
    val capacidadVehiculo: Int,
    val modeloVehiculo: Int,
    val color: String,
    val fechaMatricula: String,
    val placa : String,
    )
