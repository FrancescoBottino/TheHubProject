package com.francescobottino.thehubproject.games.tictactoe

import com.francescobottino.thehubproject.games.tictactoe.model.*
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom.State


fun TicTacToeGameState.getWinnerSign(): TicTacToePlayerSign? {
    val groups = (0..2).map { r -> (0..2).map { c -> this[TicTacToeBoardCell(r, c)] } } +  //rows
            (0..2).map { c -> (0..2).map { r -> this[TicTacToeBoardCell(r, c)] } } +       //cols
            listOf(                                                                                    //diagonals
                (0..2).map { i -> this[TicTacToeBoardCell(i, i)] },
                (0..2).map { i -> this[TicTacToeBoardCell(i, 2 - i)] }
            )

    return groups
        .firstOrNull { group -> group.none { it == null } && group.distinct().size == 1 } //group is full and all signs are the same sign
        ?.distinct()
        ?.singleOrNull() //get the sign
}

fun TicTacToeGameRoom.getWinnerPlayer(): TicTacToePlayer? {
    return gameState.getWinnerSign()?.let { winnerSign -> players.single { it.sign == winnerSign } }
}

fun TicTacToeGameState.isBoardFull(): Boolean {
    return size == 9
}

fun TicTacToeGameRoom.updateWinner(): TicTacToeGameRoom {
    val winner = getWinnerPlayer()
    return when {
        winner != null -> copy(roomState = State.Finished(winner))
        gameState.isBoardFull() -> copy(roomState = State.Finished(null))
        else -> this
    }
}