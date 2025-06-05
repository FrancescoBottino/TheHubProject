package com.francescobottino.thehubproject// In server/src/main/kotlin/your_package_name/plugins/Routing.kt (or Application.kt)
// package your_package_name.plugins // Adjust

import com.francescobottino.thehubproject.auth.HashingService
import com.francescobottino.thehubproject.auth.JwtConfig
import com.francescobottino.thehubproject.data.UserRepository
import com.francescobottino.thehubproject.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.util.*

fun Application.configureUserRouting() {
    routing {
        post("/auth/register") {
            try {
                val userRepository by closestDI().instance<UserRepository>()

                val request = call.receive<AuthRequest>() // Receives AuthRequest (username, password)
                if (userRepository.findByUsername(request.username) != null) {
                    call.respond(HttpStatusCode.Conflict, ErrorResponse("User already exists"))
                    return@post
                }

                //todo expand username and password checks. (possibly use ktor plugins)
                if (request.username.length < 3 || !request.username.matches(Regex("[a-zA-Z0-9_]+"))) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid username format"))
                    return@post
                }

                if (request.password.length < 6) { // Basic validation
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Password too short (min 6 chars)"))
                    return@post
                }

                val hashedPassword = HashingService.hashPassword(request.password)
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    username = request.username,
                    passwordHash = hashedPassword
                )
                userRepository.create(newUser)
                val token = JwtConfig.generateToken(newUser.id)
                call.respond(HttpStatusCode.Created, AuthResponse(token, "User registered successfully"))
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request format: ${e.message}"))
            } catch (e: Exception) {
                application.log.error("Registration failed", e)
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Registration failed: ${e.message}"))
            }
        }
        post("/auth/login") {
            try {
                val userRepository by closestDI().instance<UserRepository>()

                val request = call.receive<AuthRequest>()
                val user = userRepository.findByUsername(request.username)
                if (user == null || !HashingService.checkPassword(request.password, user.passwordHash)) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid username or password"))
                    return@post
                }
                val token = JwtConfig.generateToken(user.id)
                call.respond(HttpStatusCode.OK, AuthResponse(token, "Login successful"))
            } catch (e: ContentTransformationException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request format: ${e.message}"))
            } catch (e: Exception) {
                application.log.error("Login failed", e)
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Login failed: ${e.message}"))
            }
        }
        authenticate("auth-jwt") {
            get("/me") {
                requireUser {
                    call.respond(HttpStatusCode.OK, UserProfile(it.id, it.username))
                }
            }

            get("/hello-protected") {
                val userId = call.getAuthUserId()
                call.respondText("Hello, ${userId ?: "Anonymous"}! This is a protected resource.")
            }
        }
    }
}