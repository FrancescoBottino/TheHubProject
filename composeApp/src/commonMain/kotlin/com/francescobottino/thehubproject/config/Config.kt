package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.ClientConfig

object Config {
    val IS_DEV = ClientConfig.ENVIRONMENT == "dev" || (ClientConfig.ENVIRONMENT != "prod" && ClientConfig.ENVIRONMENT != "staging")
    val serverEndpoint: String = when(ClientConfig.ENVIRONMENT) {
        "dev" -> "${internalServerDebugIp(ClientConfig.DEV_SERVER_IP)}:${ClientConfig.DEV_SERVER_PORT}"
        "staging" -> ClientConfig.STAGING_SERVER_DOMAIN
        else -> ClientConfig.PROD_SERVER_DOMAIN
    }
    private val httpSchema = if(!IS_DEV) "https" else "http"
    private val wsSchema = if(!IS_DEV) "wss" else "ws"
    val httpUrl = "$httpSchema://$serverEndpoint"
    val wsUrl = "$wsSchema://$serverEndpoint"
}