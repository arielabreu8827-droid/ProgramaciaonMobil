package com.itespf.aulamobile.ui.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itespf.aulamobile.data.local.TokenManager
import com.itespf.aulamobile.data.model.GradesResponse
import com.itespf.aulamobile.data.repository.AuthRepository
import com.itespf.aulamobile.data.repository.GradesRepository
import com.itespf.aulamobile.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GradesViewModel(
    private val gradesRepository: GradesRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _gradesState = MutableStateFlow<UiState<GradesResponse>>(UiState.Idle)
    val gradesState: StateFlow<UiState<GradesResponse>> = _gradesState.asStateFlow()

    val profileName: String get() = tokenManager.getUserName() ?: "Estudiante"
    val profileUsername: String get() = tokenManager.getUsername() ?: "—"

    init {
        loadGrades()
    }

    fun loadGrades() {
        _gradesState.value = UiState.Loading
        viewModelScope.launch {
            val result = gradesRepository.getGrades()
            _gradesState.value = result
            if (result is UiState.Error && result.isUnauthorized) {
                // Token expirado: limpiamos la sesión local para forzar el
                // regreso a la pantalla de login (observado en el NavGraph).
                tokenManager.clearSession()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
