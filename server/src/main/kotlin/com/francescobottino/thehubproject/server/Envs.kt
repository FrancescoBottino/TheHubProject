package com.francescobottino.thehubproject.server

object Envs {
    val SERVER_PORT = System.getenv("SERVER_PORT").toIntOrNull() ?: 9090

    val DB_USER = System.getenv("DB_USER") ?: throw IllegalArgumentException("DB_USER environment variable not set.")
    val DB_PASSWORD = System.getenv("DB_PASSWORD") ?: throw IllegalArgumentException("DB_PASSWORD environment variable not set.")
    val DB_HOST = System.getenv("DB_HOST") ?: throw IllegalArgumentException("DB_HOST environment variable not set.")
    val DB_PORT = System.getenv("DB_PORT") ?: throw IllegalArgumentException("DB_PORT environment variable not set.")
    val DB_NAME = System.getenv("DB_NAME") ?: throw IllegalArgumentException("DB_NAME environment variable not set.")

    val JWT_SECRET = System.getenv("JWT_SECRET") ?: throw IllegalArgumentException("JWT_SECRET environment variable not set.")
    val JWT_ISSUER = System.getenv("JWT_ISSUER") ?: throw IllegalArgumentException("JWT_ISSUER environment variable not set.")
    val JWT_AUDIENCE = System.getenv("JWT_AUDIENCE") ?: throw IllegalArgumentException("JWT_AUDIENCE environment variable not set.")
}