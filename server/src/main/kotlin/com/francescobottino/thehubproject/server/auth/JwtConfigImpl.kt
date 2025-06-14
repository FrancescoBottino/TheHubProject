package com.francescobottino.thehubproject.server.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.francescobottino.thehubproject.server.Envs
import com.francescobottino.thehubproject.server_shared.auth.JwtConfig
import com.francescobottino.thehubproject.server_shared.auth.USER_ID_CLAIM
import kotlinx.datetime.Clock
import java.util.*
import kotlin.time.Duration

class JwtConfigImpl: JwtConfig {
    private val algorithm: Algorithm = Algorithm.HMAC256(Envs.JWT_SECRET)

    override fun generateToken(userId: String, expiresIn: Duration): String {
        return JWT.create()
            .withAudience(Envs.JWT_AUDIENCE)
            .withIssuer(Envs.JWT_ISSUER)
            .withClaim(USER_ID_CLAIM, userId)
            .withExpiresAt(Date((Clock.System.now() + expiresIn).toEpochMilliseconds()))
            .sign(algorithm)
    }

    override fun getVerifier(): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(Envs.JWT_AUDIENCE)
        .withIssuer(Envs.JWT_ISSUER)
        .build()
}