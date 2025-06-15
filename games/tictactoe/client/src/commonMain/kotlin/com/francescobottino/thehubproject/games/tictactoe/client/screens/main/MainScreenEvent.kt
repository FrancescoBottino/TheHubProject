package com.francescobottino.thehubproject.games.tictactoe.client.screens.main

sealed interface MainScreenEvent {
    data class OnSearchedRoomIdChanged(val newSearchedRoomId: String): MainScreenEvent
    data object OnJoinRoom: MainScreenEvent
    data object OnCreateRoom: MainScreenEvent
    data object OnSeeMyGames: MainScreenEvent
    data object OnDialogClosed: MainScreenEvent
}