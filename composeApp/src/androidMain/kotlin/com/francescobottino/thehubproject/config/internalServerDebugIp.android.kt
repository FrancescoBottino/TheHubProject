package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.utils.isRunningOnEmulator

internal actual fun internalServerDebugIp(devIp: String): String {
    return if (isRunningOnEmulator()) "10.0.2.2" else devIp
}