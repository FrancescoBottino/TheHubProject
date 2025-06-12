package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.auth.JwtConfig
import com.francescobottino.thehubproject.model.safe
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureRoutingUser() {
    route("user") {
        authenticate(JwtConfig.NAME) {
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