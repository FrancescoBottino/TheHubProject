package com.francescobottino.thehubproject.server_features.core.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val passwordHash: String
)