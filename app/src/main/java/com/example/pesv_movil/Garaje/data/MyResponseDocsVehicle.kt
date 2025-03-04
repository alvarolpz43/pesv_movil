package com.example.pesv_movil.Garaje.data

data class MyResponseDocsVehicle(
    val success: Boolean,
    val data: List<DocsData>
)


data class DocsData(
    val _id: String,
    val idVehiculo: String,
    val tipoDocumentoId: DataTipo
)

data class DataTipo(
    val _id: String,
    val nombre: String
)