package com.francescobottino.thehubproject.games.tictactoe

import cafe.adriel.voyager.core.screen.Screen
import com.francescobottino.thehubproject.GameModule
import com.francescobottino.thehubproject.games.tictactoe.screens.main.MainScreen
import org.jetbrains.compose.resources.DrawableResource
import thehubproject.game_tictactoe_client.generated.resources.Res
import thehubproject.game_tictactoe_client.generated.resources.tic_tac_toe

object TicTacToeGameModule: GameModule {
    override val name: String
        get() = "Tic Tac Toe"
    override val icon: DrawableResource
        get() = Res.drawable.tic_tac_toe
    override fun getMainScreen(): Screen = MainScreen
}