package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.ClientConfig

object Config {
    private const val PRODUCTION_ENDPOINT = "thehubproject-api.up.railway.app"
    private val serverEndpoint: String = if(ClientConfig.IS_PRODUCTION) PRODUCTION_ENDPOINT else "${internalServerDebugIp(ClientConfig.DEV_SERVER_IP)}:${ClientConfig.DEV_SERVER_PORT}"
    private val httpSchema = if(ClientConfig.IS_PRODUCTION) "https" else "http"
    private val wsSchema = if(ClientConfig.IS_PRODUCTION) "wss" else "ws"
    val httpUrl = "$httpSchema://$serverEndpoint"
    val wsUrl = "$wsSchema://$serverEndpoint"
}