package com.francescobottino.thehubproject.auth

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt(JwtConfig.NAME) {
            realm = JwtConfig.REALM
            verifier(JwtConfig.getVerifier()) // Provide the verifier
            validate { credential ->
                // This block is called when Ktor validates a token
                // 'credential.payload' contains the JWT payload
                val userId = credential.payload.getClaim(JwtConfig.USER_ID_CLAIM).asString()
                if (userId != null) {
                    // If the claim exists, create a principal.
                    // Here, we are creating a JWTPrincipal, but you can create your own custom Principal.
                    JWTPrincipal(credential.payload)
                    // Or, more usefully, fetch your User object from DB/cache if needed
                    // For now, JWTPrincipal is enough to know the userId.
                } else {
                    null // Validation fails if userId claim is missing
                }
            }
            challenge { _, _ ->
                // What to do if authentication fails (e.g., token missing or invalid)
                // We'll handle this in specific routes later or let Ktor send 401 Unauthorized.
            }
        }
    }
}