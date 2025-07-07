package com.francescobottino.thehubproject.games.tictactoe.server.repository

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom

fun TicTacToeGameRoomRepository.write(room: TicTacToeGameRoom) {
    updateRoom(room.id) { existingRoom ->
        if(existingRoom != null) {
            throw RuntimeException("Room already exists")
        }
        UpdateResult.Write(room)
    }
}