package com.francescobottino.thehubproject.games.tictactoe

import cafe.adriel.voyager.core.screen.Screen
import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.games.tictactoe.screens.main.MainScreen

object TicTacToeGameModule: GameModule {
    override fun getName(): String = "Tic Tac Toe"
    override fun getMainScreen(): Screen = MainScreen
}