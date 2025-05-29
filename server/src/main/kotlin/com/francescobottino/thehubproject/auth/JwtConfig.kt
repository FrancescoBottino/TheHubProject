package com.francescobottino.thehubproject.auth


import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.datetime.Clock
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

object JwtConfig {
    val secret: String = System.getenv("JWT_SECRET") ?: throw IllegalArgumentException("JWT_SECRET environment variable not set")
    val issuer: String = System.getenv("JWT_ISSUER") ?: throw IllegalArgumentException("JWT_ISSUER environment variable not set")
    val audience: String = System.getenv("JWT_AUDIENCE") ?: throw IllegalArgumentException("JWT_AUDIENCE environment variable not set")
    const val REALM = "Ktor Game Server"
    const val USER_ID_CLAIM = "userId"

    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: String, validity: Duration = 7.days): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim(USER_ID_CLAIM, userId)
            .withExpiresAt(Date((Clock.System.now() + validity).toEpochMilliseconds()))
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()
}