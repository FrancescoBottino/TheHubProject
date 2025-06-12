package com.francescobottino.thehubproject.games.tictactoe.di

import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.di.WEBSOCKET_ENDPOINT
import com.francescobottino.thehubproject.games.tictactoe.TicTacToeGameModule
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import org.koin.core.qualifier.named
import org.koin.dsl.module

val tictactoeModule = module {
    single<GameModule> { TicTacToeGameModule }
    single { TicTacToeApi(get(), get(named(WEBSOCKET_ENDPOINT))) }
}