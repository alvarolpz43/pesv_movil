package com.example.pesv_movil.Garaje.data

data class MyResponseVehiculo(
    val success: Boolean,
    val data: List<InfoVehicle>
)

data class InfoVehicle(
    val _id: String,
    val idUsuario: UserInfo,
    val idUsuarioAsignado: UserInfo?,
    val idClaseVehiculo: String,
    val idActividadVehiculo: ActividadVehiculo,
    val idZona: Zona,
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
data class UserInfo(
    val _id: String,
    val name: String,
)

data class ActividadVehiculo(
    val _id: String,
    val nombreTipo: String,
    val description: String
)

data class Zona(
    val _id: String,
    val nombreZona: String,
    val codeZona: String
)