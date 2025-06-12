package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.BuildConfig

object Config {
    val isProduction: Boolean by lazy { BuildConfig.ENVIRONMENT != "dev" }
    private val serverEndpoint: String by lazy {
        if(isProduction)
            BuildConfig.PRODUCTION_ENDPOINT
        else
            "${internalServerDebugIp(BuildConfig.DEV_SERVER_IP)}:${BuildConfig.DEV_SERVER_PORT}"
    }
    private val httpSchema by lazy { if(isProduction) "https" else "http" }
    private val wsSchema by lazy { if(isProduction) "wss" else "ws" }
    val httpUrl by lazy { "$httpSchema://$serverEndpoint" }
    val wsUrl by lazy { "$wsSchema://$serverEndpoint" }
}