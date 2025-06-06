package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.games.tictactoe.configureTicTacToe
import io.ktor.server.routing.*

fun Routing.configureRoutingGames() {
    route("games") {
        configureTicTacToe()
    }
}