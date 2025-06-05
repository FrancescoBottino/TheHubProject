package com.francescobottino.thehubproject.network

import com.francescobottino.thehubproject.mainJson
import com.francescobottino.thehubproject.model.ErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend inline fun <reified T> HttpClient.safeRequest(
    block: HttpClient.() -> HttpResponse
): Result<T> {
    return try {
        val response = block()
        if (response.status.isSuccess()) {
            Result.success(response.body())
        } else {
            val errorBody: String = response.body()
            val errorResponse = try {
                mainJson.decodeFromString<ErrorResponse>(errorBody)
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