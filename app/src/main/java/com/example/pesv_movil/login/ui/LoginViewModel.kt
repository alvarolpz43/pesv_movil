package com.example.pesv_movil.login.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pesv_movil.login.data.network.response.LoginRequest
import com.example.pesv_movil.login.domain.LoginUseCase
import com.example.pesv_movil.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _isLoginEnable = MutableLiveData<Boolean>()
    val isLoginEnable: LiveData<Boolean> get() = _isLoginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _loginSuccess = MutableLiveData<Boolean?>()
    val loginSuccess: LiveData<Boolean?> get() = _loginSuccess

    private val _sessionExpired = MutableLiveData<Boolean>()
    val sessionExpired: LiveData<Boolean> get() = _sessionExpired

    init {
        checkTokenValidity()
    }

    fun onLoginChanged(user: String, password: String) {
        _email.value = user
        _password.value = password
        _isLoginEnable.value = enableLogin(user, password)
    }

    private fun enableLogin(user: String, password: String): Boolean {
        return user.isNotEmpty() && password.isNotEmpty()
    }

    fun onLoginSelected() {
        val username = _email.value ?: ""
        val password = _password.value ?: ""

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loginRequest = LoginRequest(username, password)
                val response = loginUseCase(loginRequest)

                Log.d("LoginViewModel", "Token recibido: ${response.token}")

                val tokenData = parseJwt(response.token)
                val issuedAt = tokenData["iat"]?.toString()?.toLongOrNull() ?: 0L
                val expiresAt = tokenData["exp"]?.toString()?.toLongOrNull() ?: 0L

                if (issuedAt > 0 && expiresAt > 0) {
                    tokenManager.saveToken(response.token)
                    Log.d("LoginViewModel", "Token almacenado con éxito.")
                } else {
                    Log.e("LoginViewModel", "No se pudo extraer iat o exp del token")
                }

                _loginSuccess.postValue(true)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error en el inicio de sesión: ${e.message}")
                _loginSuccess.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun resetLoginState() {
        _loginSuccess.value = null
    }


    fun checkTokenValidity() {
        viewModelScope.launch {
            if (tokenManager.isTokenExpired()) {
                Log.d("LoginViewModel", "Token expirado. Cerrando sesión.")
                tokenManager.clearToken()
                _sessionExpired.postValue(true)
            }
        }
    }

    private fun parseJwt(token: String): Map<String, Any> {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("JWT", "Token inválido, no tiene 3 partes: $token")
                return emptyMap()
            }

            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_PADDING or android.util.Base64.NO_WRAP))
            Log.d("JWT", "Payload decodificado: $payload")

            val json = org.json.JSONObject(payload)
            json.keys().asSequence().associateWith { key ->
                val value = json.get(key)
                if (value is Int) value.toLong() else value
            }
        } catch (e: Exception) {
            Log.e("JWT", "Error al parsear el JWT: ${e.message}")
            emptyMap()
        }
    }
}
