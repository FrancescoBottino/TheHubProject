package com.francescobottino.thehubproject.server_features.core.auth

import com.auth0.jwt.JWTVerifier
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface JwtConfig {
    fun generateToken(userId: String, expiresIn: Duration = 7.days): String
    fun getVerifier(): JWTVerifier
}