package com.itespf.aulamobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itespf.aulamobile.data.repository.AuthRepository
import com.itespf.aulamobile.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loginState: StateFlow<UiState<Unit>> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Ingresa tu matrícula y tu contraseña.")
            return
        }
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            _loginState.value = authRepository.login(username, password)
        }
    }

    fun consumeError() {
        if (_loginState.value is UiState.Error) {
            _loginState.value = UiState.Idle
        }
    }
}
