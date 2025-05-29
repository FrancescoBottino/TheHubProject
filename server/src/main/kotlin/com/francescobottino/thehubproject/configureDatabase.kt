// In server/src/main/kotlin/your_package_name/plugins/Database.kt
package com.francescobottino.thehubproject

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase(): Database? {
    val dbUrl = environment.config.propertyOrNull("ktor.database.url")?.getString()
        ?: System.getenv("DB_URL")

    val dbUser = environment.config.propertyOrNull("ktor.database.user")?.getString()
        ?: System.getenv("DB_USER")

    val dbPassword = environment.config.propertyOrNull("ktor.database.password")?.getString()
        ?: System.getenv("DB_PASSWORD")

    val useInMemoryDb = dbUrl == null || dbUser == null || dbPassword == null || (System.getenv("USE_IN_MEMORY_DB") == "true")

    if (useInMemoryDb) {
        log.info("Using InMemory database")
        return null
    } else {
        log.info("Connecting to database at $dbUrl")
        return Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
    }
}