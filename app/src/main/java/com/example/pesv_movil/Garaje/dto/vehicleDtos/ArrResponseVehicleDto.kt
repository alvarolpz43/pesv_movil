package com.example.pesv_movil.Garaje.dto.vehicleDtos

import com.example.pesv_movil.Garaje.dto.userDtos.DataTipoDctoUsuarioDto

data class ArrResponseVehicleDto(
    val tipoDocVehiculo: List<DataTipoDctoVehicleDto>,
    val tipoDocUsuario: List<DataTipoDctoUsuarioDto>
)
