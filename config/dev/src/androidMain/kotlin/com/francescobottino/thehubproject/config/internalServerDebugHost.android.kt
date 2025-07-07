package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.isRunningOnEmulator

internal actual fun internalServerDebugHost(): String {
    return if (isRunningOnEmulator()) "10.0.2.2" else BuildConfig.DEV_SERVER_HOST
}