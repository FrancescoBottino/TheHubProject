package com.francescobottino.thehubproject.games.tictactoe.client

import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.francescobottino.thehubproject.client_shared.GameModule
import com.francescobottino.thehubproject.games.tictactoe.client.screens.main.MainScreen
import com.francescobottino.thehubproject.games.tictactoe.client.ui.images.TicTacToe

object TicTacToeGameModule: GameModule {
    override val name: String
        get() = "Tic Tac Toe"
    override val icon: ImageVector
        get() = TicTacToe
    override fun getMainScreen(): Screen = MainScreen
}