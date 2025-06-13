package com.francescobottino.thehubproject

object SharedConfig {
    val isProduction: Boolean by lazy { BuildConfig.ENVIRONMENT?.contains("dev") != true }
}