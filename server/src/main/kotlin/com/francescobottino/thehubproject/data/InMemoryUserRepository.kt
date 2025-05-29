package com.francescobottino.thehubproject.data

import com.francescobottino.thehubproject.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class InMemoryUserRepository: UserRepository {
    private val users = ConcurrentHashMap<String, User>()

    override suspend fun create(user: User): User = withContext(Dispatchers.IO) {
        users[user.id] = user
        return@withContext user
    }

    override suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        return@withContext users.values.find { it.username == username }
    }

    override suspend fun findById(id: String): User? = withContext(Dispatchers.IO) {
        return@withContext users[id] // If id is the key for this map
    }
}