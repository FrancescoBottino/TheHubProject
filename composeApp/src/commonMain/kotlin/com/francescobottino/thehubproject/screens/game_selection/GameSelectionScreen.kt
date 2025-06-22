package com.francescobottino.thehubproject.screens.game_selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.GameModule
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import com.francescobottino.thehubproject.games.tictactoe.client.TicTacToeGameModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.getKoin

object GameSelectionScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val games = getKoin().getAll<GameModule>()

        GameSelectionScreenContent(
            games = games,
            onClick = { navigator.push(it.getMainScreen()) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun GameSelectionScreenContent(
    games: List<GameModule>,
    onClick: (GameModule) -> Unit,
    modifier: Modifier = Modifier,
) {
    GamesList(
        games = games,
        onClick = onClick,
        modifier = modifier,
    )

    /*
    GamesGrid(
        games = games,
        onClick = onClick,
        modifier = modifier,
    )
    
     */
}

@Composable
private fun GamesList(
    games: List<GameModule>,
    onClick: (GameModule) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
    ) {
        Box {}
        games.forEach { game ->
            GameSelectionScreenRowItem(
                game = game,
                onClick = { onClick(game) },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            )
        }
        Box {}
    }
}

@Composable
private fun GameSelectionScreenRowItem(
    game: GameModule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Icon(
                imageVector = game.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.requiredSize(48.dp),
            )
            Text(
                text = game.name,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun GamesGrid(
    games: List<GameModule>,
    onClick: (GameModule) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cols = 2

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
    ) {
        Box {}
        for(row in 0 until games.size / cols) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
            ) {
                for(col in 0 until cols) {
                    games.getOrNull(row * cols + col)?.let { game ->
                        GameSelectionScreenSquareItem(
                            game = game,
                            onClick = { onClick(game) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
        Box {}
    }
}

@Composable
private fun GameSelectionScreenSquareItem(
    game: GameModule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
        ) {
            Icon(
                imageVector = game.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.requiredSize(48.dp),
            )

            Text(
                text = game.name,
                modifier = Modifier,
            )
        }
    }
}

@Preview
@Composable
private fun GamesGridPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            GamesGrid(
                games = (0 until 20).map { TicTacToeGameModule },
                onClick = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun GamesListPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            GamesList(
                games = (0 until 20).map { TicTacToeGameModule },
                onClick = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}