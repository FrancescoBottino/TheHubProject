package com.francescobottino.thehubproject.shared_features.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AuthResponseError {
    USER_NOT_FOUND,
    INCORRECT_PASSWORD,
    USER_ALREADY_EXISTS;
}

