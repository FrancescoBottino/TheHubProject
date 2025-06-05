package com.francescobottino.thehubproject.api.auth

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/auth")
open class AuthResource {
    @Serializable
    @Resource("register")
    class Register(val parent: AuthResource = AuthResource()): AuthResource()
    // Server expects: AuthRequest body
    // Server responds with: AuthResponse

    @Serializable
    @Resource("login")
    class Login(val parent: AuthResource = AuthResource()): AuthResource()
    // Server expects: AuthRequest body
    // Server responds with: AuthResponse
}