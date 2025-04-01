package com.example.pesv_movil.preoperacional.data

data class RequestBodyForm(
    val formularioId: String,
    val idVehiculo: String,
    val respuestas: List<RespuestaForm>
)

data class RequestBodyFormNoAplica(
    val idVehiculo: String,

)

data class RespuestaForm(
    val idPregunta: String,
    val respuesta: Boolean
)
