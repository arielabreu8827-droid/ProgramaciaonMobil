package com.itespf.aulamobile.ui.common

/**
 * Sealed class de estado de UI (Unidad 3): cada pantalla que consume la API
 * la usa para representar carga / éxito / error de forma explícita.
 */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * @param message texto a mostrar al usuario.
     * @param isUnauthorized true cuando el servidor respondió 401 (token
     * inválido/expirado). En ese caso la pantalla debe forzar el regreso al login.
     */
    data class Error(val message: String, val isUnauthorized: Boolean = false) : UiState<Nothing>()
}
