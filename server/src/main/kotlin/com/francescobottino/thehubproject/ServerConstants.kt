package com.francescobottino.thehubproject

internal object ServerConstants {

    const val DEV_SERVER_PORT = 8080

    // CORS Configuration
    const val CORS_ALLOW_CREDENTIALS = false
    const val CORS_ALLOW_ANY_HOST = true // For development. Set to false in production.
    val CORS_ALLOWED_HOSTS = listOf("your-wasm-client-domain.com") // Used when CORS_ALLOW_ANY_HOST is false
    val CORS_ALLOWED_SCHEMES = listOf("http", "https") // Used when CORS_ALLOW_ANY_HOST is false
}
