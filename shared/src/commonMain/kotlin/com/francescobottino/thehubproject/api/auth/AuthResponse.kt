package com.francescobottino.thehubproject.api.auth

import kotlinx.serialization.Serializable

@Serializable(with = AuthResponseSerializer::class)
sealed interface AuthResponse {
    val type: String

    @Serializable
    data class Success(
        val token: String,
        val userId: String,
        val username: String,
    ): AuthResponse {
        override val type: String = "AuthResponse.Success"
    }

    @Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
    @Serializable(with = AuthResponseSerializer::class)
    sealed interface Error: AuthResponse {
        val message: String
        val errorCode: String
    }

    @Serializable
    data class UserNotFound(
        override val message: String = "The specified user was not found.",
        override val errorCode: String = "USER_NOT_FOUND"
    ): Error {
        override val type: String = "AuthResponse.UserNotFound"
    }

    @Serializable
    data class IncorrectPassword(
        override val message: String = "The password provided is incorrect.",
        override val errorCode: String = "INCORRECT_PASSWORD"
    ): Error {
        override val type: String = "AuthResponse.IncorrectPassword"
    }

    @Serializable
    data class UserAlreadyExists(
        override val message: String = "A user with this username or email already exists.",
        override val errorCode: String = "USER_ALREADY_EXISTS"
    ): Error {
        override val type: String = "AuthResponse.UserAlreadyExists"
    }

    @Serializable
    data class InvalidInput(
        val errors: Map<String, String>? = null, // Optional: field-specific validation errors
        override val message: String = "The provided input is invalid.",
        override val errorCode: String = "INVALID_INPUT"
    ): Error {
        override val type: String = "AuthResponse.InvalidInput"
    }

    @Serializable
    data class GenericError(
        override val message: String,
        override val errorCode: String = "GENERIC_AUTH_ERROR"
    ): Error {
        override val type: String = "AuthResponse.GenericError"
    }
}

