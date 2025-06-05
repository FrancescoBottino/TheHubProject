package com.francescobottino.thehubproject.api.auth

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class AuthResponseSerializer: JsonContentPolymorphicSerializer<AuthResponse>(AuthResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<AuthResponse> {
        return when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "AuthResponse.Success" -> AuthResponse.Success.serializer()
            "AuthResponse.UserNotFound" -> AuthResponse.UserNotFound.serializer()
            "AuthResponse.IncorrectPassword" -> AuthResponse.IncorrectPassword.serializer()
            "AuthResponse.UserAlreadyExists" -> AuthResponse.UserAlreadyExists.serializer()
            "AuthResponse.InvalidInput" -> AuthResponse.InvalidInput.serializer()
            "AuthResponse.GenericError" -> AuthResponse.GenericError.serializer()
            else -> throw Exception("Unknown AuthResponse: key 'type' not found or does not matches any module type")
        }
    }
}