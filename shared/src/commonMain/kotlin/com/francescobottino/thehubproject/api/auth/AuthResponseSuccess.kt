package com.francescobottino.thehubproject.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseSuccess(
    val token: String,
    val userId: String,
    val username: String,
)