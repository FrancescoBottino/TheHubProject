package com.francescobottino.thehubproject.auth

import arrow.core.Either
import com.francescobottino.thehubproject.api.auth.AuthRequest
import com.francescobottino.thehubproject.api.auth.AuthResource
import com.francescobottino.thehubproject.api.auth.AuthResponseError
import com.francescobottino.thehubproject.api.auth.AuthResponseSuccess
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi(
    private val client: HttpClient,
) {
    suspend fun register(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess> {
        return authRequest(request, AuthResource.Register())
    }

    suspend fun login(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess> {
        return authRequest(request, AuthResource.Login())
    }

    private suspend inline fun <reified T: AuthResource> authRequest(request: AuthRequest, endpoint: T): Either<AuthResponseError, AuthResponseSuccess> {
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