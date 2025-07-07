package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.shared_features.core.config.SharedConfig

object Config: SharedConfig {
    private val API_ENDPOINT: String = "${internalServerDebugHost(BuildConfig.DEV_SERVER_HOST)}:${BuildConfig.DEV_SERVER_PORT}"
    override val apiHttpUrl = "http://${API_ENDPOINT}"
    override val apiWsUrl = "ws://${API_ENDPOINT}"
}