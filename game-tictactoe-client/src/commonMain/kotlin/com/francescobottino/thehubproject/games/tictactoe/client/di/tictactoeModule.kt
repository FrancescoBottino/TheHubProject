package com.francescobottino.thehubproject.games.tictactoe.client.di

import com.francescobottino.thehubproject.client_shared.GameModule
import com.francescobottino.thehubproject.client_shared.di.WEBSOCKET_ENDPOINT
import com.francescobottino.thehubproject.games.tictactoe.client.TicTacToeGameModule
import com.francescobottino.thehubproject.games.tictactoe.client.network.TicTacToeApi
import org.koin.core.qualifier.named
import org.koin.dsl.module

val tictactoeModule = module {
    single<GameModule> { TicTacToeGameModule }
    single { TicTacToeApi(get(), get(named(WEBSOCKET_ENDPOINT))) }
}