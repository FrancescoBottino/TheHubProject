package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.BuildConfig

object Config {
    val isProduction: Boolean by lazy { BuildConfig.ENVIRONMENT?.contains("dev") != true }
    private const val PRODUCTION_ENDPOINT = "thehub-8su2.onrender.com"
    private val serverEndpoint: String by lazy {
        if(isProduction)
            PRODUCTION_ENDPOINT
        else
            "${internalServerDebugIp(BuildConfig.DEV_SERVER_IP)}:${BuildConfig.DEV_SERVER_PORT}"
    }
    private val httpSchema by lazy { if(isProduction) "https" else "http" }
    private val wsSchema by lazy { if(isProduction) "wss" else "ws" }
    val httpUrl by lazy { "$httpSchema://$serverEndpoint" }
    val wsUrl by lazy { "$wsSchema://$serverEndpoint" }
}