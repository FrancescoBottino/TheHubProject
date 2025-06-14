package com.francescobottino.thehubproject.server

import com.francescobottino.thehubproject.server_shared.auth.AUTH_JWT
import com.francescobottino.thehubproject.server_shared.getAuthUser
import com.francescobottino.thehubproject.server_shared.model.safe
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureRoutingUser() {
    route("user") {
        authenticate(AUTH_JWT) {
            get("me") { getMe() }
        }
    }
}

private suspend fun RoutingContext.getMe() {
    val user = call.getAuthUser()?.safe() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }
    call.respond(HttpStatusCode.OK, user)
}