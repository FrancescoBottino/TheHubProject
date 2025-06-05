package com.francescobottino.thehubproject.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TestApiService(
    private val client: HttpClient
) {
    suspend fun getGreeting(): Result<String> {
        return runCatching { client.get("/").body() }
    }

    suspend fun getProtectedGreeting(): Result<String> {
        return runCatching { client.get("/hello-protected").body() }
    }
}