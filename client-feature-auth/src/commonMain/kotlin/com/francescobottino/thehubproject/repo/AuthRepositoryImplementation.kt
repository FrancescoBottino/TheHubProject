package com.francescobottino.thehubproject.repo

import arrow.core.Either
import com.francescobottino.thehubproject.TokenStorage
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.model.AuthResponseError
import com.francescobottino.thehubproject.model.AuthResponseSuccess
import com.francescobottino.thehubproject.security.SecureStorage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthRepositoryImplementation(
    private val client: HttpClient,
    private val secureStorage: SecureStorage
): AuthRepository {
    private val tokenStorage = TokenStorage(secureStorage)

    override suspend fun register(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess> {
        return authRequest(request, "/auth/register")
    }

    override suspend fun login(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess> {
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
        }.onRight {
            client.authProvider<BearerAuthProvider>()?.clearToken()
            tokenStorage.saveToken(it.token)
        }
    }

    override suspend fun logout() {
        client.authProvider<BearerAuthProvider>()?.clearToken()
        tokenStorage.clearToken()
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }

    override suspend fun getToken(): String? {
        return tokenStorage.getToken()
    }
}