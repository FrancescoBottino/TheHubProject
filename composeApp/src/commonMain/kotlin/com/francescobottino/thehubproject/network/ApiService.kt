package com.francescobottino.thehubproject.network

import com.francescobottino.thehubproject.Config
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.model.AuthResponse
import com.francescobottino.thehubproject.model.UserProfile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*

//todo cleanup

class ApiService(
    private val tokenStorage: TokenStorage,
    private val client: HttpClient
) {
    suspend fun register(authRequest: AuthRequest): Result<AuthResponse> {
        val result = client.safeRequest<AuthResponse> {
            post("${Config.httpUrl}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(authRequest)
            }
        }
        result.onSuccess { response ->
            tokenStorage.saveToken(response.token)
            // Optional: Force Auth plugin to reload token state if needed,
            // though it should pick up on next request that needs auth.
        }
        return result
    }

    suspend fun login(authRequest: AuthRequest): Result<AuthResponse> {
        val result = client.safeRequest<AuthResponse> {
            post("${Config.httpUrl}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(authRequest)
            }
        }
        result.onSuccess { response ->
            tokenStorage.saveToken(response.token)
        }
        return result
    }

    suspend fun logout() {
        tokenStorage.clearToken()
    }

    suspend fun getGreeting(): Result<String> {
        return try {
            val responseText: String = client.get("${Config.httpUrl}/").body()
            Result.success(responseText)
        } catch (e: Exception) {
            println("Error fetching greeting: ${e.message}")
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    suspend fun getMyProfile(): Result<UserProfile> {
        return client.safeRequest {
            get("${Config.httpUrl}/me")
        }
    }

    suspend fun getProtectedGreeting(): Result<String> {
        return client.safeRequest {
            get("${Config.httpUrl}/hello-protected")
        }
    }

    /**
     * Establishes a WebSocket session to the echo endpoint.
     * Returns the session if successful, null otherwise.
     * The caller is responsible for managing the session (sending, receiving, closing).
     */
    suspend fun establishEchoWebSocketSession(): DefaultClientWebSocketSession? {
        val wsUrl = "${Config.wsUrl}/ws/echo"
        return try {
            client.webSocketSession(urlString = wsUrl) {}
        } catch (e: Exception) {
            println("Error establishing WebSocket session to $wsUrl: ${e.message}")
            null
        }
    }

    fun close() {
        client.close()
    }
}