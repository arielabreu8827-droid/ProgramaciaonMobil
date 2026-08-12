package com.itespf.aulamobile.data.model

/** Cuerpo enviado a POST /api/v1/auth/login */
data class LoginRequest(
    val username: String,
    val password: String
)

/** Usuario devuelto dentro de la respuesta de login */
data class UserDto(
    val id: Int,
    val name: String,
    val username: String
)

/** Respuesta 200 OK de POST /api/v1/auth/login */
data class LoginResponse(
    val token: String,
    val user: UserDto
)

/** Cuerpo de error genérico que devuelve la API, p.ej. { "error": "Credenciales inválidas." } */
data class ApiErrorResponse(
    val error: String?
)
