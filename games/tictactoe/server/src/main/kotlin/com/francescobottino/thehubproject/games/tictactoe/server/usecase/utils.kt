package com.francescobottino.thehubproject.games.tictactoe.server.usecase

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom.State
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameState
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign

fun TicTacToeGameState.getWinnerSign(): TicTacToePlayerSign? {
    val rows = (0..2).map { r -> (0..2).map { c -> this[TicTacToeBoardCell(r, c)] } }
    val cols = (0..2).map { c -> (0..2).map { r -> this[TicTacToeBoardCell(r, c)] } }
    val diagonals = listOf(
        (0..2).map { i -> this[TicTacToeBoardCell(i, i)] },
        (0..2).map { i -> this[TicTacToeBoardCell(i, 2 - i)] }
    )

    val groups = rows + cols + diagonals

    return groups
        .firstOrNull { group -> group.none { it == null } && group.distinct().size == 1 } //group is full and all signs are the same sign
        ?.distinct()
        ?.singleOrNull() //get the sign
}

fun TicTacToeGameState.isBoardFull(): Boolean {
    return size == 9
}

fun TicTacToeGameRoom.updateWinner(): TicTacToeGameRoom {
    val winner = gameState.getWinnerSign()
    return when {
        winner != null -> copy(roomState = State.Finished(winner))
        gameState.isBoardFull() -> copy(roomState = State.Finished(null))
        else -> this
    }
}