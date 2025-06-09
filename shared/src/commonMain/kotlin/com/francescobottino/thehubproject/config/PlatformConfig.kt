package com.francescobottino.thehubproject.config

object PlatformConfig {
    const val isDebug: Boolean = false //todo integrate in build process
    val serverEndpoint: String by lazy { if(!isDebug) "thehub-8su2.onrender.com" else internalServerDebugEndpoint }

    private val httpSchema by lazy { if(!isDebug) "https" else "http" }
    private val wsSchema by lazy { if(!isDebug) "wss" else "ws" }

    val httpUrl by lazy { "$httpSchema://$serverEndpoint" }
    val wsUrl by lazy { "$wsSchema://$serverEndpoint" }
}