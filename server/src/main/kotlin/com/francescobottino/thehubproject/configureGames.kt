package com.francescobottino.thehubproject

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureGames() {
    routing {
        route("games") {
            //configureTicTacToe()
        }
    }
}