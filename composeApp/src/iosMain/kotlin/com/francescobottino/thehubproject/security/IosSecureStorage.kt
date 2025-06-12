package com.francescobottino.thehubproject.security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

class IosSecureStorage: SecureStorage {
    private val userDefaults = NSUserDefaults.Companion.standardUserDefaults()

    override suspend fun saveSecret(key: String, secret: String) = withContext(Dispatchers.Default) {
        userDefaults.setValue(secret, forKey = key)
    }
    override suspend fun getSecret(key: String): String? = withContext(Dispatchers.Default) {
        userDefaults.stringForKey(key)
    }
    override suspend fun clearSecret(key: String) = withContext(Dispatchers.Default) {
        userDefaults.removeObjectForKey(key)
    }
}