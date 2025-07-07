package com.francescobottino.thehubproject.games.tictactoe.client.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.francescobottino.thehubproject.games.tictactoe.client.ui.images.TicTacToeCrown
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign

@Composable
fun PastWinnersRowFull(
    winners: List<TicTacToePlayerSign?>,
    modifier: Modifier = Modifier,
) {
    PastWinnersRow_Internal(
        modifier = modifier,
    ) {
        winners.forEach { winner ->
            SignBox {
                SignIcon(winner, Modifier.fillMaxHeight().aspectRatio(1f))
            }
        }
    }
}

@Composable
fun PastWinnersRowCompact(
    winners: Map<TicTacToePlayerSign?, Int>,
    modifier: Modifier = Modifier,
) {
    PastWinnersRow_Internal(
        modifier = modifier,
    ) {
        winners.forEach { (winner, amount) ->
            SignBox {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    SignIcon(winner, Modifier.fillMaxHeight().aspectRatio(1f))
                    Text(
                        text = amount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun PastWinnersRow_Internal(
    modifier: Modifier = Modifier,
    content: @Composable FlowRowScope.() -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
        maxLines = 2,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Text(
                text = "Past Winners ",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )

            Image(
                imageVector = TicTacToeCrown,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )

            Text(
                text = ":",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }

        content()
    }
}

@Composable
private fun SignBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .requiredHeight(22.dp)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(4.dp),
    ) {
        content()
    }
}