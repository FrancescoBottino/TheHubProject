package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.auth.JwtConfig
import com.francescobottino.thehubproject.data.UserRepository
import com.francescobottino.thehubproject.model.User
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun ApplicationCall.getAuthUserId(): String? {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.getClaim(JwtConfig.USER_ID_CLAIM)?.asString()
}

suspend fun ApplicationCall.getAuthUser(): User? {
    val userRepository by closestDI().instance<UserRepository>()
    return getAuthUserId()?.let { userRepository.findById(it) }
}