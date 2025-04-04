package com.example.pesv_movil.preoperacional.data

data class ResponseVehicleSinPre(
    val success: Boolean,
    val data: List<DataVehicleSinPre>
)


data class DataVehicleSinPre(
    val _id: String,
    val idUsuario: String,
    val idUsuarioAsignado: String,
    val idClaseVehiculo: String,
    val idTipoVehiculo: String,
    val idZona: String,
    val marca: String,
    val servicio: String,
    val capacidadVehiculo: Int,
    val modeloVehiculo: Int,
    val color: String,
    val fechaMatricula: String,
    val placa: String,
    val VehicleEmpresa: Boolean,
    val vehiculoEnUso: Boolean,
    val estadoVehiculo: Boolean,
    val fechaCreacion: String,
)




data class ApiVehicleSinPreResponse(
    val success: Boolean,
    val message: String?,
    val data: List<VehiclePreoperacionalWrapper>
)

data class VehiclePreoperacionalWrapper(
    val vehiculo: VehicleApiData,
    val formularioAsignado: FormularioAsignado?,
    val debeRealizar: Boolean
)

data class VehicleApiData(
    val _id: String,
    val placa: String,
    val marca: String
    // Solo los campos que realmente vienen en la API
)

data class FormularioAsignado(
    val _id: String,
    val version: Int
)