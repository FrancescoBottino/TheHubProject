package com.francescobottino.thehubproject.shared_features.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseSuccess(
    val token: String,
    val userId: String,
    val username: String,
)