package com.francescobottino.thehubproject.data

import com.francescobottino.thehubproject.model.User

interface UserRepository {
    suspend fun create(user: User): User?
    suspend fun findByUsername(username: String): User?
    suspend fun findById(id: String): User?
}