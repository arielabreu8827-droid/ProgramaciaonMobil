package com.itespf.aulamobile.data.repository

import com.google.gson.Gson
import com.itespf.aulamobile.data.local.TokenManager
import com.itespf.aulamobile.data.model.ApiErrorResponse
import com.itespf.aulamobile.data.model.LoginRequest
import com.itespf.aulamobile.data.remote.ApiService
import com.itespf.aulamobile.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repositorio de autenticación: login real, guardado seguro del token
 * y logout real contra el servidor (no basta con borrar el token local).
 */
class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): UiState<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequest(username.trim(), password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveSession(
                        token = body.token,
                        userId = body.user.id,
                        name = body.user.name,
                        username = body.user.username
                    )
                    UiState.Success(Unit)
                } else {
                    UiState.Error("Respuesta vacía del servidor. Intenta de nuevo.")
                }
            } else if (response.code() == 401) {
                UiState.Error(parseErrorMessage(response.errorBody()?.string()) ?: "Credenciales inválidas.")
            } else {
                UiState.Error("Error del servidor (código ${response.code()}). Intenta de nuevo.")
            }
        } catch (e: IOException) {
            UiState.Error("Sin conexión. Verifica tu internet e intenta de nuevo.")
        } catch (e: Exception) {
            UiState.Error("Ocurrió un error inesperado: ${e.localizedMessage ?: "desconocido"}")
        }
    }

    /** Cierra sesión de verdad en el servidor y luego limpia el token local. */
    suspend fun logout(): UiState<Unit> = withContext(Dispatchers.IO) {
        try {
            api.logout()
        } catch (e: Exception) {
            // Si el logout remoto falla (p.ej. sin conexión), igual limpiamos
            // la sesión local para que el usuario no quede "atascado" logueado.
        }
        tokenManager.clearSession()
        UiState.Success(Unit)
    }

    private fun parseErrorMessage(rawJson: String?): String? {
        if (rawJson.isNullOrBlank()) return null
        return try {
            Gson().fromJson(rawJson, ApiErrorResponse::class.java)?.error
        } catch (e: Exception) {
            null
        }
    }
}
