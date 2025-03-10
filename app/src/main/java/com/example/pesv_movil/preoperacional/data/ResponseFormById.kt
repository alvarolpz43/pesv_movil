package com.example.pesv_movil.preoperacional.data

data class ResponseFormById(
    val success: Boolean,
    val formulario: List<DataFormulario>
)

data class DataFormulario(
    val _id: String,
    val nombreFormulario: String,
    val preguntas: List<DataPregunta>,
    val idClaseVehiculo: String,
    val version: Int,
    val estadoFormulario: Boolean,
)

data class DataPregunta(
    val _id: String,
    val preguntaTexto: String,
    val determinancia: Boolean
)
