package com.francescobottino.thehubproject.games.tictactoe.client.screens.game

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeBoardCell

sealed interface GameScreenEvent {
    data class OnBoardCellClicked(val cell: TicTacToeBoardCell): GameScreenEvent
    data object OnDismissDialog: GameScreenEvent
    data object OnDialogActionConnectToRoom: GameScreenEvent
    data object OnCloseScreen: GameScreenEvent
    data object OnCloseRoom: GameScreenEvent
    data object OnRetry: GameScreenEvent
    data object OnCopyRoomInviteLink: GameScreenEvent
}