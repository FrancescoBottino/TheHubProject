package com.francescobottino.thehubproject

object Envs {
    val HOST by lazy { System.getenv("HOST") ?: "0.0.0.0" }
    val PORT by lazy { System.getenv("PORT")?.toIntOrNull() ?: throw Exception("PORT environment variable not set.") }

    val DB_URL by lazy { System.getenv("DB_URL") ?: throw RuntimeException("DB_URL environment variable not set.") }
    val DB_USER by lazy { System.getenv("DB_USER") ?: throw RuntimeException("DB_USER environment variable not set.") }
    val DB_PASSWORD by lazy { System.getenv("DB_PASSWORD") ?: throw RuntimeException("DB_PASSWORD environment variable not set.") }

    val JWT_SECRET = System.getenv("JWT_SECRET") ?: throw IllegalArgumentException("JWT_SECRET environment variable not set.")
    val JWT_ISSUER = System.getenv("JWT_ISSUER") ?: throw IllegalArgumentException("JWT_ISSUER environment variable not set.")
    val JWT_AUDIENCE = System.getenv("JWT_AUDIENCE") ?: throw IllegalArgumentException("JWT_AUDIENCE environment variable not set.")
}