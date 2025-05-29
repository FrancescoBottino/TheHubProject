package com.francescobottino.thehubproject.auth

/**
 * Interface for securely storing and retrieving authentication tokens.
 */
interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}