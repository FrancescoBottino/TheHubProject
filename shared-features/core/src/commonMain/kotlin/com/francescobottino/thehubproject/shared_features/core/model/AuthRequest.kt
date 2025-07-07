package com.francescobottino.thehubproject.shared_features.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String, val password: String)