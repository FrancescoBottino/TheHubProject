package com.francescobottino.thehubproject


object Config {
    const val IS_DEBUG = true
    const val DEBUG_PORT = 8080
    const val DEBUG_IP = "localhost"
    const val DEBUG_ENDPOINT = "$DEBUG_IP:$DEBUG_PORT"
    const val PROD_ENDPOINT = "thehub-8su2.onrender.com"

    val ENDPOINT = if(IS_DEBUG) DEBUG_ENDPOINT else PROD_ENDPOINT

    private val secure = !IS_DEBUG
    private val httpSchema = if(secure) "https" else "http"
    private val wsSchema = if(secure) "wss" else "ws"

    val httpUrl = "$httpSchema://$ENDPOINT/"
    val wsUrl = "$wsSchema://$ENDPOINT/"
}