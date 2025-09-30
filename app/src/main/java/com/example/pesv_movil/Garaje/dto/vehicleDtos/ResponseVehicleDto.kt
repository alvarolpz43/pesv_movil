package com.example.pesv_movil.Garaje.dto.vehicleDtos

data class ResponseVehicleDto(
    val success: Boolean,
    val message: String,
    val data: List<InfoVehicleDto> = emptyList()
)

