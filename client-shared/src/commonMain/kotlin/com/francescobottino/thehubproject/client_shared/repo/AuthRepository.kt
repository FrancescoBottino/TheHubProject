package com.francescobottino.thehubproject.client_shared.repo

import arrow.core.Either
import com.francescobottino.thehubproject.shared.model.AuthRequest
import com.francescobottino.thehubproject.shared.model.AuthResponseError
import com.francescobottino.thehubproject.shared.model.AuthResponseSuccess

interface AuthRepository {
    suspend fun register(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess>
    suspend fun login(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getToken(): String?
}