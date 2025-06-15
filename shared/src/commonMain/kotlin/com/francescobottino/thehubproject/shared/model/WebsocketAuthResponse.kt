package com.francescobottino.thehubproject.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketAuthResponse(
    val isSuccess: Boolean,
)