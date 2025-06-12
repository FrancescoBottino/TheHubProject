package com.francescobottino.thehubproject.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.francescobottino.thehubproject.Envs
import kotlinx.datetime.Clock
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

object JwtConfig {
    val algorithm: Algorithm = Algorithm.HMAC256(Envs.JWT_SECRET)

    fun generateToken(userId: String, validity: Duration = 7.days): String {
        return JWT.create()
            .withAudience(Envs.JWT_AUDIENCE)
            .withIssuer(Envs.JWT_ISSUER)
            .withClaim(USER_ID_CLAIM, userId)
            .withExpiresAt(Date((Clock.System.now() + validity).toEpochMilliseconds()))
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(Envs.JWT_AUDIENCE)
        .withIssuer(Envs.JWT_ISSUER)
        .build()
}