package com.francescobottino.thehubproject.auth

import com.francescobottino.thehubproject.security.SecureStorage

class TokenStorage(
    private val secureStorage: SecureStorage,
) {
    companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
    }
    suspend fun saveToken(token: String) {
        secureStorage.saveSecret(KEY_AUTH_TOKEN, token)
    }
    suspend fun getToken(): String? {
        return secureStorage.getSecret(KEY_AUTH_TOKEN)
    }
    suspend fun clearToken() {
        secureStorage.clearSecret(KEY_AUTH_TOKEN)
    }
}