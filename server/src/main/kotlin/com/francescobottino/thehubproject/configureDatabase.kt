package com.francescobottino.thehubproject

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase(): Database {
    try {
        // Make sure the driver class is loaded
        Class.forName("org.postgresql.Driver")
    } catch (e: ClassNotFoundException) {
        log.error("PostgreSQL JDBC driver not found in dependency tree")
        throw e
    }

    return Database.connect(
        driver = "org.postgresql.Driver",
        url = Envs.DB_URL,
        user = Envs.DB_USER,
        password = Envs.DB_PASSWORD,
    )
}