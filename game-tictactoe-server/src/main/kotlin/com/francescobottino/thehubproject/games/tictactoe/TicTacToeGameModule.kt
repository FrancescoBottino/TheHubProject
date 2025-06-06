package com.francescobottino.thehubproject.games.tictactoe

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayer
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToePlayerSign
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TicTacToeGameModule(
    private val repo: TicTacToeGameRoomRepository,
) {
    fun makeRoom(playerId: String, chosenSign: TicTacToePlayerSign, startingSign: TicTacToePlayerSign): String {
        val room = TicTacToeGameRoom(
            id = Uuid.random().toString(),
            hostPlayer = TicTacToePlayer(
                id = playerId,
                sign = chosenSign,
            ),
            currentPlayerSign = startingSign
        )

        repo.storeRoom(room)

        return room.id
    }

    fun joinRoom(playerId: String, roomId: String) {
        repo.updateRoom(roomId) {
            val room = it ?: throw IllegalStateException("Room not found")

            require(room.opponentPlayer == null) { "Room already has an opponent" }
            require(room.hostPlayer.id != playerId) { "Cannot join your own room" }

            room.copy(
                opponentPlayer = TicTacToePlayer(
                    id = playerId,
                    sign = room.hostPlayer.sign.otherSign(),
                ),
                roomState = TicTacToeGameRoom.State.InProgress,
            )
        }
    }

    fun makeMove(playerId: String, roomId: String, cell: TicTacToeBoardCell) {
        repo.updateRoom(roomId) {
            var room = it ?: throw IllegalStateException("Room not found")

            val player = room.players.singleOrNull { it.id == playerId }
            require(player != null) { "Player not found" }
            require(room.currentPlayerSign == player.sign) { "Not your turn" }

            require(room.roomState is TicTacToeGameRoom.State.InProgress) { "Game not in progress" }

            val newGameState = room.gameState + (cell to player.sign)

            room = room.copy(
                gameState = newGameState,
                currentPlayerSign = player.sign.otherSign(),
            )

            room = room.copy(
                roomState = room.isGameOver() ?: TicTacToeGameRoom.State.InProgress
            )

            room
        }
    }

    fun restartGame(playerId: String, roomId: String) {
        repo.updateRoom(roomId) {
            val room = it ?: throw IllegalStateException("Room not found")

            val roomState = room.roomState
            require(roomState is TicTacToeGameRoom.State.Finished) { "Game not finished" }
            require(playerId == room.hostPlayer.id) { "Only the host can restart the game" }

            room.copy(
                gameState = emptyMap(),
                roomState = TicTacToeGameRoom.State.InProgress,
                pastGamesWinners = room.pastGamesWinners + roomState.winner
            )
        }
    }

    fun close(playerId: String, roomId: String) {
        repo.updateRoom(roomId) {
            val room = it ?: throw IllegalStateException("Room not found")

            val player = room.players.singleOrNull { it.id == playerId }
            require(player != null) { "Player not found" }

            room.copy(
                roomState = TicTacToeGameRoom.State.Closed(player),
            )
        }
    }
}