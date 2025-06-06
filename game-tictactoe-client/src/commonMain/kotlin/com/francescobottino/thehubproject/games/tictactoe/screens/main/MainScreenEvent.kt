package com.francescobottino.thehubproject.games.tictactoe.screens.main

sealed interface MainScreenEvent {
    data class OnSearchedRoomIdChanged(val newSearchedRoomId: String): MainScreenEvent
    data object OnJoinRoom: MainScreenEvent
    data object OnCreateRoom: MainScreenEvent
}