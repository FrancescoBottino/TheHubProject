package com.francescobottino.thehubproject

object ServerConfig {
    val isProduction: Boolean by lazy { System.getenv("ENVIRONMENT") != "dev" }
    val host: String by lazy { System.getenv("HOST") ?: "0.0.0.0" }
    val port: Int by lazy { System.getenv("PORT")?.toIntOrNull() ?: throw Exception("PORT environment variable not set.") }
}