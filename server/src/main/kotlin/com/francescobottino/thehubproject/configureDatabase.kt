package com.francescobottino.thehubproject

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase(): Database? {
    if (System.getenv("USE_IN_MEMORY_DB") == "true") {
        log.info("Using InMemory database")
        return null
    }

    try {
        // Make sure the driver class is loaded
        Class.forName("org.postgresql.Driver")
    } catch (e: ClassNotFoundException) {
        log.error("PostgreSQL JDBC driver not found in dependency tree")
        throw e
    }

    val dbUrl = environment.config.propertyOrNull("ktor.database.url")?.getString()
        ?: System.getenv("DB_URL")
        ?: throw RuntimeException("Missing DB_URL environment variable")

    val dbUser = environment.config.propertyOrNull("ktor.database.user")?.getString()
        ?: System.getenv("DB_USER")
        ?: throw RuntimeException("Missing DB_USER environment variable")

    val dbPassword = environment.config.propertyOrNull("ktor.database.password")?.getString()
        ?: System.getenv("DB_PASSWORD")
        ?: throw RuntimeException("Missing DB_PASSWORD environment variable")

    return Database.connect(
        url = dbUrl,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword,
    )
}