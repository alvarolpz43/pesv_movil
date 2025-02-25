package com.example.pesv_movil.components

data class MyResponseSelects(
    val success: Boolean,
    val zonas: List<ZonasList>,
    val clases: List<ClasesList>,
    val tipos: List<TiposList>,
    val servicio: List<ServiciosList>
)


data class ZonasList(
    val _id: String,
    val nombreZona: String,
    val codeZona: String
)

data class ClasesList(
    val _id: String,
    val name: String,
    val description: String
)

data class TiposList(
    val _id: String,
    val nombreTipo: String,
    val description: String
)

data class ServiciosList(
    val _id: String,
    val name: String,
)
