package com.francescobottino.thehubproject.games.tictactoe

import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.games.tictactoe.presentation.TicTacToe
import com.francescobottino.thehubproject.games.tictactoe.screens.main.MainScreen

object TicTacToeGameModule: GameModule {
    override val name: String
        get() = "Tic Tac Toe"
    override val icon: ImageVector
        get() = TicTacToe
    override fun getMainScreen(): Screen = MainScreen
}