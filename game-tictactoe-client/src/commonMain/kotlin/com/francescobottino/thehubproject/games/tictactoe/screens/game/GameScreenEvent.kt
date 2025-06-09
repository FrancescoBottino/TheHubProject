package com.francescobottino.thehubproject.games.tictactoe.screens.game

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell

sealed interface GameScreenEvent {
    data class OnUserClickedCell(val cell: TicTacToeBoardCell): GameScreenEvent
}