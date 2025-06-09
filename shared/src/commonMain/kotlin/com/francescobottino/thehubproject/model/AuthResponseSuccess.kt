package com.francescobottino.thehubproject.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseSuccess(
    val token: String,
    val userId: String,
    val username: String,
)