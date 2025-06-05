package com.francescobottino.thehubproject.api.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
)