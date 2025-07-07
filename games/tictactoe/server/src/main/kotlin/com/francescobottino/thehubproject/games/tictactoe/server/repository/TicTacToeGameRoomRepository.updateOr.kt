package com.francescobottino.thehubproject.games.tictactoe.server.repository

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom

fun TicTacToeGameRoomRepository.updateRoomOrSkip(roomId: String, updater: (TicTacToeGameRoom?) -> TicTacToeGameRoom?) {
    updateRoom(roomId) { room ->
        updater(room)?.let { UpdateResult.Write(it) } ?: UpdateResult.Skip()
    }
}

fun TicTacToeGameRoomRepository.updateRoomOrDelete(roomId: String, updater: (TicTacToeGameRoom?) -> TicTacToeGameRoom?) {
    updateRoom(roomId) { room ->
        updater(room)?.let { UpdateResult.Write(it) } ?: UpdateResult.Delete()
    }
}
