package com.francescobottino.thehubproject.network

import arrow.core.Either
import com.francescobottino.thehubproject.shared_features.core.model.UserResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class UserApi(
    private val client: HttpClient
) {
    suspend fun me(): Either<HttpStatusCode, UserResponse> {
        return client.get("/user/me") {
            contentType(ContentType.Application.Json)
        }.let {
            when {
                it.status.isSuccess() -> Either.Right(it.body<UserResponse>())
                else -> Either.Left(it.status)
            }
        }
    }
}