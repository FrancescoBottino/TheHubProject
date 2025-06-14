package com.francescobottino.thehubproject.server_shared.data

import com.francescobottino.thehubproject.server_shared.model.User

interface UserRepository {
    suspend fun create(user: User): User?
    suspend fun findByUsername(username: String): User?
    suspend fun findById(id: String): User?
}