package com.francescobottino.thehubproject.games.tictactoe.screens.game

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell

sealed interface GameScreenEvent {
    data class OnBoardCellClicked(val cell: TicTacToeBoardCell): GameScreenEvent
    data object DismissDialog: GameScreenEvent
    data object DialogActionConnectToRoom: GameScreenEvent
    data object CloseScreen: GameScreenEvent
}