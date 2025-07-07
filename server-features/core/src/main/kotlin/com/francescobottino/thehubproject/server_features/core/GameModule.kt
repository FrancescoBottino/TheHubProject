package com.francescobottino.thehubproject.server_features.core

import io.ktor.server.routing.*

interface GameModule {
    fun Route.configure()
}