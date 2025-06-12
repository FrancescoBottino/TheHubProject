package com.francescobottino.thehubproject

import com.francescobottino.thehubproject.games.tictactoe.TicTacToeGameModule
import io.ktor.server.routing.*

fun Routing.configureRoutingGames() {
    val gameModules = listOf<GameModule>(
        TicTacToeGameModule
    )

    route("games") {
        gameModules.forEach { it.run { configure() } }
    }
}