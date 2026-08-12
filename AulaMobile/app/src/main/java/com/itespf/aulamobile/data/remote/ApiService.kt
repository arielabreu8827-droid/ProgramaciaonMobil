package com.itespf.aulamobile.data.remote

import com.itespf.aulamobile.data.model.GradesResponse
import com.itespf.aulamobile.data.model.LoginRequest
import com.itespf.aulamobile.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Define las rutas reales de la API de Aula Mobile.
 * El header "Authorization: Bearer <token>" se agrega automáticamente
 * mediante [com.itespf.aulamobile.data.remote.AuthInterceptor], no aquí.
 */
interface ApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("api/v1/grades")
    suspend fun getGrades(): Response<GradesResponse>
}
