package com.francescobottino.thehubproject.server_shared.model

import com.francescobottino.thehubproject.shared.model.UserResponse

fun User.safe(): UserResponse {
    return UserResponse(
        id = id,
        username = username
    )
}