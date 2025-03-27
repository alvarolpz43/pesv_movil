package com.example.pesv_movil.preoperacional

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pesv_movil.Garaje.data.VehiculeRequest
import com.example.pesv_movil.core.network.RetrofitHelper
import com.example.pesv_movil.data.ApiService
import com.example.pesv_movil.preoperacional.data.ResponseVehicleSinPre
import com.example.pesv_movil.utils.TokenManager
import kotlinx.coroutines.flow.first
import retrofit2.Call

class PreoperacionalViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val apiService: ApiService = RetrofitHelper.getRetrofit().create(ApiService::class.java)

    suspend fun getVehiclesInUse() {
        val userToken = tokenManager.token.first() ?: ""
        val response = apiService.getVehicleSinPre("Bearer $userToken");
        Log.i("Respnse about vehiculos en uso", response.toString())
    }


}


