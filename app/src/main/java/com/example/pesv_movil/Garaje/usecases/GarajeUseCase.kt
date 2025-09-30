package com.example.pesv_movil.Garaje.usecases

import com.example.pesv_movil.Garaje.dto.vehicleDtos.ArrResponseVehicleDto
import com.example.pesv_movil.Garaje.dto.DocsDataDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.InfoVehicleDto
import com.example.pesv_movil.Garaje.repositories.GarajeRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class GarajeUseCase @Inject constructor(
    private val repository: GarajeRepository
) {
    fun getMyVechicles(): Flow<List<InfoVehicleDto>> {
        return repository.getMyVehicles()
    }

    fun changeStateVehicle(idVehiculo: String): Flow<Unit> {
        return repository.changeStateVehicle(idVehiculo)
    }

    fun submitDocumentVehicle(
        documento: MultipartBody.Part?,
        tipoDocumentoId: RequestBody,
        idVehiculo: RequestBody,
        fechaExpiracion: RequestBody,
        numeroDocumento: RequestBody
    ): Flow<Unit> {
        return repository.submitDocumentVehicle(
            documento,
            tipoDocumentoId,
            idVehiculo,
            fechaExpiracion,
            numeroDocumento
        )
    }

    fun getDocumentsVehicle(id: String): Flow<List<DocsDataDto>> {
        return repository.getDocumentsVehicle(id)
    }

    fun getTypeDocumentVehiclesAndUser(): Flow<ArrResponseVehicleDto> {
        return repository.getTipoDctoVehicleAndUser()
    }

}
