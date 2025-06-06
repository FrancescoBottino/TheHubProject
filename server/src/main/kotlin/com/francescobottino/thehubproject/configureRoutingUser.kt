package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.api.user.UserResource
import com.francescobottino.thehubproject.api.user.UserResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configureRoutingUser() {
    authenticate("auth-jwt") {
        get<UserResource.Me> { getMe(it) }
    }
}

private suspend fun RoutingContext.getMe(route: UserResource.Me) {
    val user = call.getAuthUser() ?: run {
        call.respond(HttpStatusCode.Unauthorized)
        return
    }
    call.respond(HttpStatusCode.OK, UserResponse(user.id, user.username))
}