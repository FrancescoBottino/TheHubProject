package com.francescobottino.thehubproject.games.tictactoe.client.screens.main

data class MainScreenState(
    val searchedRoomId: String = "",
    val isLoading: Boolean = false,
    val dialogMessagesQueue: List<String> = emptyList(),
)