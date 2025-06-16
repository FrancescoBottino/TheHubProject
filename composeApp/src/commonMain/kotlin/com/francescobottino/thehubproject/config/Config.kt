package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.ClientConfig

object Config {
    val isProduction: Boolean = ClientConfig.ENVIRONMENT?.contains("dev") != true
    private const val PRODUCTION_ENDPOINT = "thehubproject-api.up.railway.app"
    private val serverEndpoint: String = if(isProduction) PRODUCTION_ENDPOINT else "${internalServerDebugIp(ClientConfig.DEV_SERVER_IP)}:${ClientConfig.DEV_SERVER_PORT}"
    private val httpSchema = if(isProduction) "https" else "http"
    private val wsSchema = if(isProduction) "wss" else "ws"
    val httpUrl = "$httpSchema://$serverEndpoint"
    val wsUrl = "$wsSchema://$serverEndpoint"
}