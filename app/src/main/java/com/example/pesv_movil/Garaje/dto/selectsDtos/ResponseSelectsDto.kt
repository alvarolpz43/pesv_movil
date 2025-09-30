package com.example.pesv_movil.Garaje.dto.selectsDtos

data class ResponseSelectsDto(
    val success: Boolean,
    val zonas: List<ZonasListDto>,
    val clases: List<ClasesListDto>,
    val tipos: List<TiposListDto>,
    val servicio: List<ServiciosListDto>
)