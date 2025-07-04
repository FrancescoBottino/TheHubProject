package com.francescobottino.thehubproject.config

object Config {
    const val CLEAR_TEXT_TRAFFIC = true
    private val SERVER_ENDPOINT: String = "${internalServerDebugHost(BuildConfig.DEV_SERVER_HOST)}:${BuildConfig.DEV_SERVER_PORT}"
    val httpUrl = "http://$SERVER_ENDPOINT"
    val wsUrl = "ws://$SERVER_ENDPOINT"
}