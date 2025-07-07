package com.francescobottino.thehubproject.server.auth

import com.francescobottino.thehubproject.server.Envs
import com.francescobottino.thehubproject.server_features.core.auth.AUTH_JWT
import com.francescobottino.thehubproject.server_features.core.auth.JwtConfig
import com.francescobottino.thehubproject.server_features.core.auth.USER_ID_CLAIM
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun AuthenticationConfig.configureJwt(jwtConfig: JwtConfig) {
    jwt(AUTH_JWT) {
        realm = "Ktor Game Server"
        verifier(jwtConfig.getVerifier())
        validate { credential ->
            // This block is called when Ktor validates a token
            // 'credential.payload' contains the JWT payload

            if(!credential.payload.audience.contains(Envs.JWT_AUDIENCE)) {
                return@validate null
            }

            if(credential.payload.issuer != Envs.JWT_ISSUER) {
                return@validate null
            }

            if (credential.payload.getClaim(USER_ID_CLAIM).asString() == null) {
                return@validate null
            }

            // If the claim exists, create a principal.
            // Here, we are creating a JWTPrincipal, but you can create your own custom Principal.
            JWTPrincipal(credential.payload)
            // Or, more usefully, fetch your User object from DB/cache if needed
            // For now, JWTPrincipal is enough to know the userId.
        }
        challenge { _, _ ->
            // What to do if authentication fails (e.g., token missing or invalid)
            // We'll handle this in specific routes later or let Ktor send 401 Unauthorized.
        }
    }
}