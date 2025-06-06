package com.francescobottino.thehubproject.config

import com.francescobottino.thehubproject.utils.isRunningOnEmulator

internal actual val internalServerDebugEndpoint: String
    get() = if (isRunningOnEmulator())
        "http://10.0.2.2:9090"
    else
        "http://192.168.1.195:9090"