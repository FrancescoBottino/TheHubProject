package com.francescobottino.thehubproject.config

object PlatformConfig {
    const val isDebug: Boolean = true //todo integrate in build process
    val serverEndpoint: String = if(!isDebug)
        "thehub-8su2.onrender.com"
    else
        internalServerDebugEndpoint

    private val httpSchema = if(!isDebug) "https" else "http"
    private val wsSchema = if(!isDebug) "wss" else "ws"

    val httpUrl = "$httpSchema://$serverEndpoint"
    val wsUrl = "$wsSchema://$serverEndpoint"
}