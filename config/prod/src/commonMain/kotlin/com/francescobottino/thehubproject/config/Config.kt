package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.shared_features.core.config.SharedConfig

object Config: SharedConfig {
    override val apiHttpUrl = "https://${BuildConfig.API_DOMAIN}"
    override val apiWsUrl = "wss://${BuildConfig.API_DOMAIN}"
}