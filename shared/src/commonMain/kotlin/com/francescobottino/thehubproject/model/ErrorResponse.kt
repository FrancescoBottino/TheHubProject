package com.francescobottino.thehubproject.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)