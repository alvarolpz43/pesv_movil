package com.example.pesv_movil.Garaje.data

data class MyResponseVehiculo(
    val success: Boolean,
    val data: List<InfoVehicle>
)

data class InfoVehicle(
    val _id: String,
    val idUsuario: String,
    val idUsuarioAsignado: String?,
    val idClaseVehiculo: String,
    val idTipoVehiculo: String,
    val idZona: String,
    val marca: String,
    val servicio: String,
    val capacidadVehiculo: Int,
    val modeloVehiculo: Int,
    val color: String,
    val fechaMatricula: String,
    val placa: String,
    val VehicleEmpresa: Boolean,
    val vehiculoEnUso: Boolean,
    val estadoVehiculo: Boolean,
    val fechaCreacion: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)