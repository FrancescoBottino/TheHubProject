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
        url = "jdbc:postgresql://${Envs.DB_HOST}:${Envs.DB_PORT}/${Envs.DB_NAME}",
        user = Envs.DB_USER,
        password = Envs.DB_PASSWORD,
    )
}