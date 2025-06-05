package com.francescobottino.thehubproject.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)