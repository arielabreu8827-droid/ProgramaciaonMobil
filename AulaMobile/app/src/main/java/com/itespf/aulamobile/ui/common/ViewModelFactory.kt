package com.itespf.aulamobile.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itespf.aulamobile.data.util.ServiceLocator
import com.itespf.aulamobile.ui.grades.GradesViewModel
import com.itespf.aulamobile.ui.login.LoginViewModel

/**
 * Factory sencilla que construye los ViewModels inyectándoles sus
 * repositorios desde el ServiceLocator (sin necesidad de Hilt/Dagger).
 */
class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(ServiceLocator.authRepository) as T

            modelClass.isAssignableFrom(GradesViewModel::class.java) ->
                GradesViewModel(ServiceLocator.gradesRepository, ServiceLocator.authRepository, ServiceLocator.tokenManager) as T

            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
