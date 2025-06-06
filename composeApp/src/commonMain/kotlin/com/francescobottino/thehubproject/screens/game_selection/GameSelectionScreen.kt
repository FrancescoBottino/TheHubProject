package com.francescobottino.thehubproject.screens.game_selection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.GameModule
import org.jetbrains.compose.resources.painterResource
import org.kodein.di.compose.localDI
import org.kodein.di.instance

object GameSelectionScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val navigator = LocalNavigator.currentOrThrow

        val games: Set<GameModule> by di.instance()

        Column(

        ) {
            games.forEach { game ->
                Button({
                    navigator.push(game.getMainScreen())
                }) {
                    Row {
                        Icon(painterResource(game.icon), null, modifier = Modifier.size(24.dp))
                        Text(game.name)
                    }
                }
            }
        }
    }
}