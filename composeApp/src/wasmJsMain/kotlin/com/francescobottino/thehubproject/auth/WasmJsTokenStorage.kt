package com.francescobottino.thehubproject.auth

import kotlinx.browser.localStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.get
import org.w3c.dom.set

class WasmJsTokenStorage : TokenStorage {
    private companion object { const val KEY_AUTH_TOKEN = "auth_token" }

    override suspend fun saveToken(token: String) = withContext(Dispatchers.Default) {
        localStorage[KEY_AUTH_TOKEN] = token
    }
    override suspend fun getToken(): String? = withContext(Dispatchers.Default) {
        localStorage[KEY_AUTH_TOKEN]
    }
    override suspend fun clearToken() = withContext(Dispatchers.Default) {
        localStorage.removeItem(KEY_AUTH_TOKEN)
    }
}