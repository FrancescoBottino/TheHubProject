package com.francescobottino.thehubproject.server_features.core.model

import com.francescobottino.thehubproject.shared_features.core.model.UserResponse

fun User.safe(): UserResponse {
    return UserResponse(
        id = id,
        username = username
    )
}