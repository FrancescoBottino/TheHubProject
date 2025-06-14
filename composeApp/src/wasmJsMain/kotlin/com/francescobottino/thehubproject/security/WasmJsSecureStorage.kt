package com.francescobottino.thehubproject.security

import com.francescobottino.thehubproject.client_shared.security.SecureStorage
import kotlinx.browser.localStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.get
import org.w3c.dom.set

class WasmJsSecureStorage: SecureStorage {

    override suspend fun saveSecret(key: String, secret: String): Unit = withContext(Dispatchers.Default) {
        localStorage[key] = secret
    }

    override suspend fun getSecret(key: String): String? = withContext(Dispatchers.Default) {
        localStorage[key]
    }

    override suspend fun clearSecret(key: String): Unit = withContext(Dispatchers.Default) {
        localStorage.removeItem(key)
    }
}