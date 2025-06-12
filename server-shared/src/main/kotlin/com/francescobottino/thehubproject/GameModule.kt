package com.francescobottino.thehubproject

import io.ktor.server.routing.*

interface GameModule {
    fun Route.configure()
}