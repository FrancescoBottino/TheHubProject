package com.francescobottino.thehubproject.repo

import arrow.core.Either
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.model.AuthResponseError
import com.francescobottino.thehubproject.model.AuthResponseSuccess

interface AuthRepository {
    suspend fun register(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess>
    suspend fun login(request: AuthRequest): Either<AuthResponseError, AuthResponseSuccess>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getToken(): String?
}