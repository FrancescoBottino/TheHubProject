package com.francescobottino.thehubproject.network

import com.francescobottino.thehubproject.Config
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.mainJson
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*

fun makeHttpClient(
    tokenStorage: TokenStorage
): HttpClient {
    return HttpClient {
        expectSuccess = false
        install(Resources)
        install(ContentNegotiation) {
            json(mainJson)
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
        defaultRequest {
            url(Config.httpUrl)
        }
    }
}