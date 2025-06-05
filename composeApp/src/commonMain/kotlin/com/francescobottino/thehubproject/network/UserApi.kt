package com.francescobottino.thehubproject.network

import com.francescobottino.thehubproject.api.user.UserResource
import com.francescobottino.thehubproject.api.user.UserResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*

//TODO REFACTOR

class UserApi(
    private val client: HttpClient
) {
    suspend fun me(): Result<UserResponse> {
        return runCatching { client.get(UserResource.Me()).body() }
    }
}