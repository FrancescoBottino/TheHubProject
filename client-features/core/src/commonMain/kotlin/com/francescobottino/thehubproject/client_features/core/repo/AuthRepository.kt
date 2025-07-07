package com.francescobottino.thehubproject.client_features.core.repo

import arrow.core.Either
import com.francescobottino.thehubproject.shared_features.core.model.AuthRequest
import com.francescobottino.thehubproject.shared_features.core.model.AuthResponseError
import com.francescobottino.thehubproject.shared_features.core.model.AuthResponseSuccess

interface AuthRepository {
    suspend fun register(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess>
    suspend fun login(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getToken(): String?
}