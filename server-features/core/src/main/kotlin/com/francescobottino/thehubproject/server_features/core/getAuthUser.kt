package com.francescobottino.thehubproject.server_features.core

import com.francescobottino.thehubproject.server_features.core.auth.USER_ID_CLAIM
import com.francescobottino.thehubproject.server_features.core.data.UserRepository
import com.francescobottino.thehubproject.server_features.core.model.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun ApplicationCall.getAuthUserId(): String? {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.getClaim(USER_ID_CLAIM)?.asString()
}

suspend fun ApplicationCall.getAuthUser(): User? {
    val userRepository by inject<UserRepository>()
    return getAuthUserId()?.let { userRepository.findById(it) }
}