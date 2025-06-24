package com.francescobottino.thehubproject.games.tictactoe.client.ui.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.francescobottino.thehubproject.games.tictactoe.client.ui.images.TicTacToeCatGame
import com.francescobottino.thehubproject.games.tictactoe.client.ui.images.TicTacToeCircle
import com.francescobottino.thehubproject.games.tictactoe.client.ui.images.TicTacToeCross
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign

@Composable
fun SignIcon(
    sign: TicTacToePlayerSign?,
    modifier: Modifier = Modifier,
) {
    val signDrawable = when (sign) {
        TicTacToePlayerSign.O -> TicTacToeCircle
        TicTacToePlayerSign.X -> TicTacToeCross
        null -> TicTacToeCatGame
    }
    val color = when (sign) {
        TicTacToePlayerSign.O -> Color(0xFFFF5C00)
        TicTacToePlayerSign.X -> Color(0xFF305CDE)
        null -> Color.Black
    }
    Icon(
        imageVector = signDrawable,
        contentDescription = null,
        tint = color,
        modifier = modifier,
    )
}