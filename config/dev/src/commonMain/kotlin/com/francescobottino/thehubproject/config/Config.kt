package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.shared_features.core.config.SharedConfig

object Config: SharedConfig {
    private val hostIp = internalServerDebugHost(BuildConfig.DEV_SERVER_HOST)
    private val hostFePort = BuildConfig.DEV_API_PORT
    private val hostApiPort = BuildConfig.DEV_FE_PORT
    private val FE_ENDPOINT: String = "$hostIp:$hostFePort"
    private val API_ENDPOINT: String = "$hostIp:$hostApiPort"

    override val feHttpUrl = "http://${FE_ENDPOINT}"
    override val apiHttpUrl = "http://${API_ENDPOINT}"
    override val apiWsUrl = "ws://${API_ENDPOINT}"
}