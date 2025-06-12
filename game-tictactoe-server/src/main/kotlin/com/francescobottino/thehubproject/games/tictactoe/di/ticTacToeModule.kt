package com.francescobottino.thehubproject.games.tictactoe.di

import com.francescobottino.thehubproject.games.tictactoe.repository.TicTacToeExposedGameRoomRepository
import com.francescobottino.thehubproject.games.tictactoe.repository.TicTacToeGameRoomRepository
import com.francescobottino.thehubproject.games.tictactoe.usecase.TicTacToeUseCases
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ticTacToeModule = module {
    single<TicTacToeGameRoomRepository> { TicTacToeExposedGameRoomRepository(get()) }
    singleOf(::TicTacToeUseCases)
}