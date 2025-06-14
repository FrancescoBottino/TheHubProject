package com.francescobottino.thehubproject.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)