package com.example.pesv_movil.Garaje.dto.vehicleDtos

import com.example.pesv_movil.Garaje.data.ActividadVehiculo
import com.example.pesv_movil.Garaje.dto.userDtos.UserInfoDto
import com.example.pesv_movil.Garaje.dto.ZonaDto

data class InfoVehicleDto(
    val _id: String,
    val idUsuario: UserInfoDto,
    val idUsuarioAsignado: UserInfoDto?,
    val idClaseVehiculo: String,
    val idActividadVehiculo: ActividadVehiculo,
    val idZona: ZonaDto,
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
