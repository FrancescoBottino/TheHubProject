package com.francescobottino.thehubproject.server.auth

import org.mindrot.jbcrypt.BCrypt

object HashingService {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun checkPassword(password: String, hashed: String): Boolean {
        return BCrypt.checkpw(password, hashed)
    }
}