package com.francescobottino.thehubproject.network

import com.francescobottino.thehubproject.Config
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class Api {
    private val client = HttpClient {
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
    }

    suspend fun getGreeting() = runCatching { client.get(Config.httpUrl).body<String>() }
        .onFailure { println("Error fetching greeting: ${it.message}") }

    fun close() {
        client.close()
    }
}