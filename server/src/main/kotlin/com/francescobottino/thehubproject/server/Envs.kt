package com.francescobottino.thehubproject.server

object Envs {
    val HOST by lazy { System.getenv("HOST") ?: "0.0.0.0" }
    val PORT by lazy { System.getenv("PORT")?.toIntOrNull() ?: 80 }

    val DB_USER by lazy { System.getenv("DB_USER") ?: throw RuntimeException("DB_USER environment variable not set.") }
    val DB_PASSWORD by lazy { System.getenv("DB_PASSWORD") ?: throw RuntimeException("DB_PASSWORD environment variable not set.") }
    val DB_HOST by lazy { System.getenv("DB_HOST") ?: throw RuntimeException("DB_HOST environment variable not set.") }
    val DB_PORT by lazy { System.getenv("DB_PORT") ?: throw RuntimeException("DB_PORT environment variable not set.") }
    val DB_NAME by lazy { System.getenv("DB_NAME") ?: throw RuntimeException("DB_NAME environment variable not set.") }

    val JWT_SECRET = System.getenv("JWT_SECRET") ?: throw IllegalArgumentException("JWT_SECRET environment variable not set.")
    val JWT_ISSUER = System.getenv("JWT_ISSUER") ?: throw IllegalArgumentException("JWT_ISSUER environment variable not set.")
    val JWT_AUDIENCE = System.getenv("JWT_AUDIENCE") ?: throw IllegalArgumentException("JWT_AUDIENCE environment variable not set.")
}