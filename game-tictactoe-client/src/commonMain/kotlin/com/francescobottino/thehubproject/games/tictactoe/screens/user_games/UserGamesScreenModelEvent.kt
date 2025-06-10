package com.francescobottino.thehubproject.games.tictactoe.screens.user_games

import cafe.adriel.voyager.core.screen.Screen

sealed interface UserGamesScreenModelEvent {
    data class Navigate(val screen: Screen): UserGamesScreenModelEvent
}