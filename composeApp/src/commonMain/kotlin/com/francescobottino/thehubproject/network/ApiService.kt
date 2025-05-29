package com.francescobottino.thehubproject.network

import com.francescobottino.thehubproject.Config
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.model.AuthResponse
import com.francescobottino.thehubproject.model.ErrorResponse
import com.francescobottino.thehubproject.model.UserProfile
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiService(
    private val tokenStorage: TokenStorage
) {
    private val client = HttpClient {
        expectSuccess = false
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(WebSockets) {}

        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenStorage.getToken()
                    if (token != null) {
                        BearerTokens(token, "")
                    } else {
                        null
                    }
                }
                refreshTokens {
                    println("Refresh token logic not implemented yet.")
                    null
                }
                // Optional: Send token for specific hosts or paths
                // sendWithoutRequest { request -> request.url.host == Url(baseUrl).host }
            }
        }
    }

    private suspend inline fun <reified T> safeRequest(
        block: HttpClient.() -> HttpResponse
    ): Result<T> {
        return try {
            val response = client.block()
            if (response.status.isSuccess()) {
                Result.success(response.body())
            } else {
                val errorBody: String = response.body()
                val errorResponse = try {
                    Json.decodeFromString<ErrorResponse>(errorBody)
                } catch (e: Exception) {
                    ErrorResponse("HTTP ${response.status.value}: ${response.status.description} - $errorBody")
                }
                Result.failure(Exception(errorResponse.error))
            }
        } catch (e: Exception) {
            println("Network request failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }

    suspend fun register(authRequest: AuthRequest): Result<AuthResponse> {
        val result = safeRequest<AuthResponse> {
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
        val result = safeRequest<AuthResponse> {
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
        return safeRequest {
            get("${Config.httpUrl}/me")
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