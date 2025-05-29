package com.francescobottino.thehubproject.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(val id: String, val username: String)