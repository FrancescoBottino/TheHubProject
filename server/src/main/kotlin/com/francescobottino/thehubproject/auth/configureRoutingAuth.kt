package com.francescobottino.thehubproject.auth

import com.francescobottino.thehubproject.api.auth.AuthRequest
import com.francescobottino.thehubproject.api.auth.AuthResource
import com.francescobottino.thehubproject.api.auth.AuthResponse
import com.francescobottino.thehubproject.data.UserRepository
import com.francescobottino.thehubproject.model.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import kotlinx.serialization.SerializationException
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.*

fun Routing.configureRoutingAuth() {
    post<AuthResource.Register> { register(it) }
    post<AuthResource.Login> { login(it) }
}

private suspend fun RoutingContext.register(route: AuthResource.Register) {
    try {
        val request = call.receive<AuthRequest>()
        val userRepository by closestDI().instance<UserRepository>()

        if (userRepository.findByUsername(request.username) != null) {
            call.respond(HttpStatusCode.Conflict, AuthResponse.UserAlreadyExists())
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

        call.respond(HttpStatusCode.Created, AuthResponse.Success(token = token, userId = newUser.id, username = newUser.username))
    } catch (e: SerializationException) {
        call.respond(HttpStatusCode.BadRequest, AuthResponse.InvalidInput(message = "Invalid request body: ${e.localizedMessage}"))
    } catch (e: Exception) {
        call.application.log.error("Registration failed", e)
        call.respond(HttpStatusCode.InternalServerError, AuthResponse.GenericError("An unexpected error occurred."))
    }
}

private suspend fun RoutingContext.login(route: AuthResource.Login) {
    try {
        val request = call.receive<AuthRequest>()
        val userRepository by closestDI().instance<UserRepository>()

        val user = userRepository.findByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Unauthorized, AuthResponse.UserNotFound())
            return
        }

        val passwordCorrect = HashingService.checkPassword(request.password, user.passwordHash)
        if(!passwordCorrect) {
            call.respond(HttpStatusCode.Unauthorized, AuthResponse.IncorrectPassword())
            return
        }

        val token = JwtConfig.generateToken(user.id)
        call.respond(HttpStatusCode.Created, AuthResponse.Success(token = token, userId = user.id, username = user.username))
    } catch (e: SerializationException) {
        call.respond(HttpStatusCode.BadRequest, AuthResponse.InvalidInput(message = "Invalid request body: ${e.localizedMessage}"))
    } catch (e: Exception) {
        call.application.log.error("Registration failed", e)
        call.respond(HttpStatusCode.InternalServerError, AuthResponse.GenericError("An unexpected error occurred."))
    }
}