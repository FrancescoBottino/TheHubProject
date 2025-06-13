package com.francescobottino.thehubproject.games.tictactoe.repository

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import kotlinx.coroutines.flow.Flow

interface TicTacToeGameRoomRepository {
    fun storeRoom(room: TicTacToeGameRoom)
    fun getRoom(roomId: String): TicTacToeGameRoom?
    fun updateRoom(roomId: String, updater: (TicTacToeGameRoom?) -> TicTacToeGameRoom?)
    fun getRoomUpdates(roomId: String): Flow<TicTacToeGameRoom>
    fun deleteRoom(roomId: String)
    fun getRoomsOfUser(userId: String): List<TicTacToeGameRoom>
}