package com.francescobottino.thehubproject.server

import com.francescobottino.thehubproject.games.tictactoe.server.TicTacToeGameModule
import com.francescobottino.thehubproject.server_shared.GameModule
import io.ktor.server.routing.*

fun Routing.configureRoutingGames() {
    val gameModules = listOf<GameModule>(
        TicTacToeGameModule
    )

    route("games") {
        gameModules.forEach { it.run { configure() } }
    }
}