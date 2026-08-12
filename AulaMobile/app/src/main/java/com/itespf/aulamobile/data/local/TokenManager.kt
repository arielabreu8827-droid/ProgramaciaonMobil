package com.itespf.aulamobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Guarda la sesión (token + datos básicos del usuario) usando
 * EncryptedSharedPreferences (androidx.security.crypto), NUNCA texto plano,
 * tal como exige el enunciado del proyecto.
 */
class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _isLoggedIn = MutableStateFlow(prefs.getString(KEY_TOKEN, null) != null)
    /** Los observadores (NavGraph) reaccionan aquí para volver al login si la sesión se cierra o expira. */
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun saveSession(token: String, userId: Int, name: String, username: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_USERNAME, username)
            .apply()
        _isLoggedIn.value = true
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserName(): String? = prefs.getString(KEY_NAME, null)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    /** Limpia la sesión local. Se llama tras un logout real contra el servidor
     * o cuando el servidor responde 401 (token expirado). */
    fun clearSession() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
    }

    companion object {
        private const val PREFS_FILE_NAME = "aula_mobile_secure_prefs"
        private const val KEY_TOKEN = "key_token"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_NAME = "key_name"
        private const val KEY_USERNAME = "key_username"
    }
}
