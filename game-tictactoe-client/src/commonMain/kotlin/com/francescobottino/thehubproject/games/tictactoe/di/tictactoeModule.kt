package com.francescobottino.thehubproject.games.tictactoe.di

import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.games.tictactoe.TicTacToeGameModule
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import org.kodein.di.*

val tictactoeModule = DI.Module("games.tictactoe") {
    inBindSet<GameModule> { add { singleton { TicTacToeGameModule } } }

    bindSingleton { TicTacToeApi(instance()) }
}