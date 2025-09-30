package com.example.pesv_movil.Garaje.usecases

import com.example.pesv_movil.Garaje.dto.selectsDtos.ResponseSelectsDto
import com.example.pesv_movil.Garaje.dto.vehicleDtos.VehicleRequestDto
import com.example.pesv_movil.Garaje.repositories.FormVehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FormVehicleUseCase @Inject constructor(private val repository: FormVehicleRepository) {

    fun createVehicle(body: VehicleRequestDto): Flow<Unit> = flow {
        repository.createVehicle(body)
    }

    fun getSelectsData(): Flow<ResponseSelectsDto> = flow {
        repository.getSelectsData()
    }
}