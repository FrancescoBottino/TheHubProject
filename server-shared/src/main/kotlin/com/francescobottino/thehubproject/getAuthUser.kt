package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.auth.JwtConfig
import com.francescobottino.thehubproject.data.UserRepository
import com.francescobottino.thehubproject.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
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

suspend fun RoutingContext.requireUserId(
    onNotAuthenticated: suspend () -> Unit = {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
    },
    onAuthenticated: suspend (String) -> Unit,
) {
    call.getAuthUserId()
        ?.let { userId -> onAuthenticated(userId) }
        ?: run { onNotAuthenticated() }
}

suspend fun RoutingContext.requireUser(
    onNotAuthenticated: suspend () -> Unit = {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
    },
    onAuthenticated: suspend (User) -> Unit,
) {
    call.getAuthUser()
        ?.let { user -> onAuthenticated(user) }
        ?: run { onNotAuthenticated() }
}

suspend fun DefaultWebSocketServerSession.requireUserId(
    onNotAuthenticated: suspend () -> Unit = {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
    },
    onAuthenticated: suspend (String) -> Unit,
) {
    call.getAuthUserId()
        ?.let { userId -> onAuthenticated(userId) }
        ?: run { onNotAuthenticated() }
}

suspend fun DefaultWebSocketServerSession.requireUser(
    onNotAuthenticated: suspend () -> Unit = {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
    },
    onAuthenticated: suspend (User) -> Unit,
) {
    call.getAuthUser()
        ?.let { user -> onAuthenticated(user) }
        ?: run { onNotAuthenticated() }
}