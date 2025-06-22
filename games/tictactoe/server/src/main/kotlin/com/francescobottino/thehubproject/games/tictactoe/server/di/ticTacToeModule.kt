package com.francescobottino.thehubproject.games.tictactoe.server.di

import com.francescobottino.thehubproject.games.tictactoe.server.repository.TicTacToeGameRoomRepository
import com.francescobottino.thehubproject.games.tictactoe.server.usecase.TicTacToeUseCases
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ticTacToeModule = module {
    singleOf(::TicTacToeGameRoomRepository)
    singleOf(::TicTacToeUseCases)
}