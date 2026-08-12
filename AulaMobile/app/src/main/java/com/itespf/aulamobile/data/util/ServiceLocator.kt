package com.itespf.aulamobile.data.util

import android.content.Context
import com.itespf.aulamobile.data.local.TokenManager
import com.itespf.aulamobile.data.remote.ApiService
import com.itespf.aulamobile.data.remote.RetrofitClient
import com.itespf.aulamobile.data.repository.AuthRepository
import com.itespf.aulamobile.data.repository.GradesRepository

/**
 * Contenedor manual de dependencias (patrón Service Locator). Mantiene la
 * separación red / datos / UI (MVVM) sin necesidad de un framework de
 * inyección de dependencias, para que el flujo sea fácil de seguir.
 */
object ServiceLocator {

    lateinit var tokenManager: TokenManager
        private set
    private lateinit var apiService: ApiService

    lateinit var authRepository: AuthRepository
        private set
    lateinit var gradesRepository: GradesRepository
        private set

    fun init(context: Context) {
        if (::tokenManager.isInitialized) return
        tokenManager = TokenManager(context.applicationContext)
        apiService = RetrofitClient.create(tokenManager)
        authRepository = AuthRepository(apiService, tokenManager)
        gradesRepository = GradesRepository(apiService)
    }
}
