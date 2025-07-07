package com.francescobottino.thehubproject.shared_features.core.model

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketAuthResponse(
    val isSuccess: Boolean,
)