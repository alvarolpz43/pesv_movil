package com.example.pesv_movil.Garaje.data

import android.net.Uri

data class FormDataDocumentation(
    val tipoDocumentoId: String,
    val idVehiculo: String,
    val fechaExpiracion: String,
    val numeroDocumento: String,
    val documento: Uri?
)
