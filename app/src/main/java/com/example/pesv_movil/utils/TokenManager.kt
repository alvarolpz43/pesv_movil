package com.example.pesv_movil.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("auth_prefs")

@Singleton
class TokenManager @Inject constructor(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

    fun getUserIdBlocking(): String? {
        return runBlocking { getUserId() }
    }

    suspend fun getUserId(): String? {
        val tokenString = token.first()
        if (tokenString.isNullOrBlank()) return null

        try {
            val parts = tokenString.split(".")
            if (parts.size != 3) return null

            val payload = String(
                Base64.decode(
                    parts[1],
                    Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )
            )
            val json = JSONObject(payload)
            return json.optString("userId", null)
        } catch (e: Exception) {
            Log.e("TokenManager", "Error al obtener el id del token: ${e.message}")
            return null
        }
    }


    suspend fun isTokenExpired(): Boolean {
        val tokenString = token.first()
        if (tokenString.isNullOrBlank()) return true

        try {
            val parts = tokenString.split(".")
            if (parts.size != 3) return true

            val payload = String(
                Base64.decode(
                    parts[1],
                    Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                )
            )
            val json = JSONObject(payload)

            val exp = json.optLong("exp", 0)
            val currentTime = System.currentTimeMillis() / 1000

            return exp < currentTime
        } catch (e: Exception) {
            Log.e("TokenManager", "Error al verificar la expiración del token: ${e.message}")
            return true
        }
    }

    fun isTokenExpiredBlocking(): Boolean {
        return runBlocking {
            return@runBlocking isTokenExpired()
        }
    }

}
