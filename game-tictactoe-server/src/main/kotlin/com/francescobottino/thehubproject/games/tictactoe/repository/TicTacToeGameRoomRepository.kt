package com.francescobottino.thehubproject.games.tictactoe.repository

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import kotlinx.coroutines.flow.Flow

interface TicTacToeGameRoomRepository {
    fun storeRoom(room: TicTacToeGameRoom)
    fun getRoom(id: String): TicTacToeGameRoom?
    fun getRoomUpdates(id: String): Flow<TicTacToeGameRoom>
    fun deleteRoom(id: String)
    fun getRoomsOfUser(userId: String): List<TicTacToeGameRoom>
}