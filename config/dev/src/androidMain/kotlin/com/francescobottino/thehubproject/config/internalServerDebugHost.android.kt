package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.isRunningOnEmulator

internal actual fun internalServerDebugHost(devIp: String): String {
    return if (isRunningOnEmulator()) "10.0.2.2" else devIp
}