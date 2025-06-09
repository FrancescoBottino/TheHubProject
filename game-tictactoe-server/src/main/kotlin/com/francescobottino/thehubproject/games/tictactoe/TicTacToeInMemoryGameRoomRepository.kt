package com.francescobottino.thehubproject.games.tictactoe

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class TicTacToeInMemoryGameRoomRepository: TicTacToeGameRoomRepository {
    private val memoryStateFlow = MutableStateFlow<Map<String, TicTacToeGameRoom>>(emptyMap())

    override fun storeRoom(room: TicTacToeGameRoom) {
        memoryStateFlow.update { memory -> memory + (room.id to room) }
    }
    override fun getRoom(id: String): TicTacToeGameRoom? {
        return memoryStateFlow.value[id]
    }
    override fun updateRoom(roomId: String, updater: (TicTacToeGameRoom?) -> TicTacToeGameRoom?) {
        memoryStateFlow.update { memory ->
            val mutableMemory = memory.toMutableMap()

            updater(mutableMemory[roomId])?.let {
                mutableMemory[roomId] = it
            } ?: run {
                mutableMemory.remove(roomId)
            }

            mutableMemory.toMap()
        }
    }
    override fun getRoomUpdates(id: String): Flow<TicTacToeGameRoom> {
        return memoryStateFlow.mapNotNull { memory -> memory[id] }
    }
    override fun deleteRoom(id: String) {
        memoryStateFlow.update { memory -> memory - id }
    }
}