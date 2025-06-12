package com.francescobottino.thehubproject.games.tictactoe.screens.game

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell

sealed interface GameScreenEvent {
    data class OnBoardCellClicked(val cell: TicTacToeBoardCell): GameScreenEvent
    data object OnDismissDialog: GameScreenEvent
    data object OnDialogActionConnectToRoom: GameScreenEvent
    data object OnCloseScreen: GameScreenEvent
    data object OnCloseRoom: GameScreenEvent
    data object OnRetry: GameScreenEvent
    data object OnCopyRoomId: GameScreenEvent
}