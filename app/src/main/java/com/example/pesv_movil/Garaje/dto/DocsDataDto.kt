package com.example.pesv_movil.Garaje.dto

import com.example.pesv_movil.Garaje.data.DataTipo

data class DocsDataDto(
    val _id: String,
    val idVehiculo: String,
    val tipoDocumentoId: DataTipoDto
)
