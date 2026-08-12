package com.itespf.aulamobile.data.remote

import com.itespf.aulamobile.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor de OkHttp que agrega automáticamente el header
 * "Authorization: Bearer <token>" a TODAS las llamadas salientes,
 * como exige el enunciado (sección 2). Las rutas de login no necesitan
 * token, así que si no hay uno guardado simplemente no se agrega el header.
 */
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val requestToSend = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(requestToSend)
    }
}
