package com.francescobottino.thehubproject.security

import com.francescobottino.thehubproject.client_features.core.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences

class DesktopSecureStorage: SecureStorage {
    private val prefs: Preferences = Preferences.userRoot().node(this::class.java.name)

    override suspend fun saveSecret(key: String, secret: String) = withContext(Dispatchers.IO) {
        prefs.put(key, secret)
        prefs.flush()
    }

    override suspend fun getSecret(key: String): String? = withContext(Dispatchers.IO) {
        prefs.get(key, null)
    }

    override suspend fun clearSecret(key: String) = withContext(Dispatchers.IO) {
        prefs.remove(key);
        prefs.flush()
    }
}