package com.francescobottino.thehubproject.auth

import com.francescobottino.thehubproject.api.auth.AuthRequest
import com.francescobottino.thehubproject.api.auth.AuthResource
import com.francescobottino.thehubproject.api.auth.AuthResponseError
import com.francescobottino.thehubproject.api.auth.AuthResponseSuccess
import com.francescobottino.thehubproject.data.UserRepository
import com.francescobottino.thehubproject.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.*

fun Routing.configureRoutingAuth() {
    post<AuthResource.Register> { register(it) }
    post<AuthResource.Login> { login(it) }
}

private suspend fun RoutingContext.register(route: AuthResource.Register) {
    val userRepository by closestDI().instance<UserRepository>()

    val request = try {
        call.receive<AuthRequest>()
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    try {
        if (userRepository.findByUsername(request.username) != null) {
            call.respond(HttpStatusCode.Conflict, AuthResponseError.USER_ALREADY_EXISTS)
            return
        }

        //todo expand username and password checks. (possibly use ktor validation)

        val hashedPassword = HashingService.hashPassword(request.password)
        val newUser = User(
            id = UUID.randomUUID().toString(),
            username = request.username,
            passwordHash = hashedPassword
        )
        userRepository.create(newUser)
        val token = JwtConfig.generateToken(newUser.id)

        call.respond(HttpStatusCode.Created, AuthResponseSuccess(token = token, userId = newUser.id, username = newUser.username))
    } catch (e: Exception) {
        call.application.log.error("Registration failed", e)
        call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
    }
}

private suspend fun RoutingContext.login(route: AuthResource.Login) {
    val userRepository by closestDI().instance<UserRepository>()

    val request = try {
        call.receive<AuthRequest>()
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    try {
        val user = userRepository.findByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, AuthResponseError.USER_NOT_FOUND)
            return
        }

        val passwordCorrect = HashingService.checkPassword(request.password, user.passwordHash)
        if(!passwordCorrect) {
            call.respond(HttpStatusCode.Unauthorized, AuthResponseError.INCORRECT_PASSWORD)
            return
        }

        val token = JwtConfig.generateToken(user.id)
        call.respond(HttpStatusCode.Created, AuthResponseSuccess(token = token, userId = user.id, username = user.username))
    } catch (e: Exception) {
        call.application.log.error("Registration failed", e)
        call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unknown error")
    }
}