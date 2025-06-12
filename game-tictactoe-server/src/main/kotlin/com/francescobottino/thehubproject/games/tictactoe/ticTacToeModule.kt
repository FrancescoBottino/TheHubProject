package com.francescobottino.thehubproject.games.tictactoe

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ticTacToeModule = module {
    single<TicTacToeGameRoomRepository> { TicTacToeInMemoryGameRoomRepository() } //todo use proper db
    singleOf(::TicTacToeUseCases)
}