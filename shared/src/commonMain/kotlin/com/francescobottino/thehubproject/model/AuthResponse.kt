package com.francescobottino.thehubproject.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val token: String, val message: String)