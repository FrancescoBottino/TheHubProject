package com.francescobottino.thehubproject.api.user

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/user")
open class UserResource {
    @Serializable
    @Resource("me")
    class Me(val parent: UserResource = UserResource()): UserResource()
    // Server expects: nothing
    // Server responds with: UserResponse
}