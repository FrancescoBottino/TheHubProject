package com.francescobottino.thehubproject.auth

import com.francescobottino.thehubproject.data.UserRepository
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.model.AuthResponseError
import com.francescobottino.thehubproject.model.AuthResponseSuccess
import com.francescobottino.thehubproject.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.*

fun Routing.configureRoutingAuth() {
    route("auth") {
        post("register") { register() }
        post("login") { login() }
    }
}

private suspend fun RoutingContext.register() {
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

private suspend fun RoutingContext.login() {
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