package com.francescobottino.thehubproject.games.tictactoe.di

import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.games.tictactoe.TicTacToeGameModule
import com.francescobottino.thehubproject.games.tictactoe.network.TicTacToeApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val tictactoeModule = module {
    single<GameModule> { TicTacToeGameModule }
    singleOf(::TicTacToeApi)
}