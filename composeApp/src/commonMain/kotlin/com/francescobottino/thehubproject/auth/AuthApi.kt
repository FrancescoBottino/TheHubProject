package com.francescobottino.thehubproject.auth

import arrow.core.Either
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.model.AuthResponseError
import com.francescobottino.thehubproject.model.AuthResponseSuccess
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi(
    private val client: HttpClient,
) {
    suspend fun register(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess> {
        return authRequest(request, "/auth/register")
    }

    suspend fun login(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess> {
        return authRequest(request, "/auth/login")
    }

    private suspend fun authRequest(request: AuthRequest, endpoint: String): Either<AuthResponseError, AuthResponseSuccess> {
        return client.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.let {
            when {
                it.status.isSuccess() -> Either.Right(it.body<AuthResponseSuccess>())
                else -> Either.Left(it.body<AuthResponseError>())
            }
        }
    }
}