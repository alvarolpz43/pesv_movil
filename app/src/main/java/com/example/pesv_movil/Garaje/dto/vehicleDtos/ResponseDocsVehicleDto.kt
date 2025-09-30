package com.example.pesv_movil.Garaje.dto.vehicleDtos

import com.example.pesv_movil.Garaje.dto.DocsDataDto

data class ResponseDocsVehicleDto(
    val success: Boolean,
    val data: List<DocsDataDto>
)
