package com.francescobottino.thehubproject.model

fun User.safe(): UserResponse {
    return UserResponse(
        id = id,
        username = username
    )
}