// In server/src/main/kotlin/your_package_name/plugins/Database.kt
package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.data.ExposedUserRepository
import com.francescobottino.thehubproject.data.InMemoryUserRepository
import com.francescobottino.thehubproject.data.UserRepository
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureUserRepository(database: Database?): UserRepository {
    return if (database != null) {
        ExposedUserRepository(database)
    } else {
        InMemoryUserRepository()
    }
}