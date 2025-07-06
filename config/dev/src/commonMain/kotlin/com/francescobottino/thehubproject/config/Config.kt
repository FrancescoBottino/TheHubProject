package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.shared.config.SharedConfig

object Config: SharedConfig {
    private val API_ENDPOINT: String = "${internalServerDebugHost(BuildConfig.DEV_SERVER_HOST)}:${BuildConfig.DEV_SERVER_PORT}"
    override val apiHttpUrl = "https://${API_ENDPOINT}"
    override val apiWsUrl = "wss://${API_ENDPOINT}"
}