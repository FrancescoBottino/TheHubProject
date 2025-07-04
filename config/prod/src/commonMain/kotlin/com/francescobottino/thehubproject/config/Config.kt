package com.francescobottino.thehubproject.config

object Config {
    const val CLEAR_TEXT_TRAFFIC = false
    val httpUrl = "https://${BuildConfig.DOMAIN}"
    val wsUrl = "wss://${BuildConfig.DOMAIN}"
}