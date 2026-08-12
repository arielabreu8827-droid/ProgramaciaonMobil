package com.itespf.aulamobile.data.repository

import com.itespf.aulamobile.data.model.GradesResponse
import com.itespf.aulamobile.data.remote.ApiService
import com.itespf.aulamobile.ui.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Repositorio que obtiene toda la información académica del estudiante
 * (GET /api/v1/grades) y traduce los distintos errores posibles a un
 * UiState.Error legible para el usuario.
 */
class GradesRepository(private val api: ApiService) {

    suspend fun getGrades(): UiState<GradesResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.getGrades()
            when {
                response.isSuccessful && response.body() != null -> {
                    UiState.Success(response.body()!!)
                }
                response.code() == 401 -> {
                    UiState.Error("Tu sesión expiró. Inicia sesión de nuevo.", isUnauthorized = true)
                }
                response.code() == 403 -> {
                    UiState.Error("Esta cuenta no tiene permiso para ver calificaciones (no es de estudiante).")
                }
                else -> {
                    UiState.Error("No se pudo cargar la información (código ${response.code()}).")
                }
            }
        } catch (e: IOException) {
            UiState.Error("Sin conexión. Verifica tu internet e intenta de nuevo.")
        } catch (e: Exception) {
            UiState.Error("Ocurrió un error inesperado: ${e.localizedMessage ?: "desconocido"}")
        }
    }
}
