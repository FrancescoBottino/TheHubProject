package com.francescobottino.thehubproject

object ServerConfig {
    val productionEndpoint: String by lazy {
        System.getenv("PRODUCTION_ENDPOINT")
    }

    val isProduction: Boolean by lazy {
        System.getProperty("environment") != "dev" &&
            System.getenv("ENVIRONMENT") != "dev" &&
            System.getenv("RENDER") == null // Render.com sets this
    }

    val host: String by lazy {
        System.getenv("HOST") ?: "0.0.0.0"
    }

    val port: Int by lazy {
        System.getenv("PORT")?.toIntOrNull() ?: throw Exception("PORT environment variable not set.")
    }
}