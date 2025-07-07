package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.shared_features.core.config.SharedConfig

object Config: SharedConfig {
    private val hostIp = internalServerDebugHost()
    private val FE_ENDPOINT: String = "$hostIp:${BuildConfig.DEV_FE_PORT}"
    private val API_ENDPOINT: String = "$hostIp:${BuildConfig.DEV_API_PORT}"

    override val feHttpUrl = "http://${FE_ENDPOINT}"
    override val apiHttpUrl = "http://${API_ENDPOINT}"
    override val apiWsUrl = "ws://${API_ENDPOINT}"
}