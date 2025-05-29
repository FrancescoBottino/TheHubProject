package com.francescobottino.thehubproject.model // Adjust package

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)