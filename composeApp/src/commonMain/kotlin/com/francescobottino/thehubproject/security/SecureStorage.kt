package com.francescobottino.thehubproject.security

/**
 * Interface for securely storing and retrieving secrets, such as API keys or access tokens.
 */
interface SecureStorage {
    suspend fun saveSecret(key: String, secret: String)
    suspend fun getSecret(key: String): String?
    suspend fun clearSecret(key: String)
}