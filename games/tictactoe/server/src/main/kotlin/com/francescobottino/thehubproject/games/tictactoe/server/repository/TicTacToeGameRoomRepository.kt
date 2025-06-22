package com.francescobottino.thehubproject.games.tictactoe.server.repository

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import kotlinx.coroutines.flow.Flow

interface TicTacToeGameRoomRepository {
    fun getRoom(roomId: String): TicTacToeGameRoom?
    fun updateRoom(roomId: String, updater: (TicTacToeGameRoom?) -> TicTacToeGameRoom?)
    fun getRoomUpdates(roomId: String): Flow<TicTacToeGameRoom>
    fun deleteRoom(roomId: String)
    fun getRoomsOfUser(userId: String): List<TicTacToeGameRoom>
}