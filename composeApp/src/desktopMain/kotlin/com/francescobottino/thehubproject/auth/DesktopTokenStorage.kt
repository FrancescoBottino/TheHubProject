package com.francescobottino.thehubproject.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences

class DesktopTokenStorage: TokenStorage {
    private val prefs: Preferences = Preferences.userRoot().node(this::class.java.name)
    private companion object { const val KEY_AUTH_TOKEN = "auth_token_desktop" }

    override suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        prefs.put(KEY_AUTH_TOKEN, token); prefs.flush()
    }
    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        prefs.get(KEY_AUTH_TOKEN, null)
    }
    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        prefs.remove(KEY_AUTH_TOKEN); prefs.flush()
    }
}