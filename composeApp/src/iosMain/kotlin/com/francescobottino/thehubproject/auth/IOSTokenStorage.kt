package com.francescobottino.thehubproject.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

class IOSTokenStorage: TokenStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults()
    private companion object { const val KEY_AUTH_TOKEN = "auth_token" }

    override suspend fun saveToken(token: String) = withContext(Dispatchers.Default) {
        userDefaults.setValue(token, forKey = KEY_AUTH_TOKEN)
    }
    override suspend fun getToken(): String? = withContext(Dispatchers.Default) {
        userDefaults.stringForKey(KEY_AUTH_TOKEN)
    }
    override suspend fun clearToken() = withContext(Dispatchers.Default) {
        userDefaults.removeObjectForKey(KEY_AUTH_TOKEN)
    }
}