package com.example.pesv_movil.Garaje.dto.vehicleDtos

data class VehicleRequestDto(
    val idClaseVehiculo: String,
    val idActividadVehiculo: String,
    val idZona: String,
    val marca: String,
    val servicio: String,
    val capacidadVehiculo: Int,
    val modeloVehiculo: String,
    val color: String,
    val fechaMatricula: String,
    val placa: String,
)
