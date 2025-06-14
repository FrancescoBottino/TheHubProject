package com.francescobottino.thehubproject.server_shared

import io.ktor.server.routing.*

interface GameModule {
    fun Route.configure()
}