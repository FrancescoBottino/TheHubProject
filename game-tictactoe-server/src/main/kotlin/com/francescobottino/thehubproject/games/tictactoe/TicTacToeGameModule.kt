package com.francescobottino.thehubproject.games.tictactoe

import arrow.core.Either
import com.francescobottino.thehubproject.games.tictactoe.model.*
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TicTacToeGameModule(
    private val repo: TicTacToeGameRoomRepository,
) {
    fun getMyRooms(userId: String): List<TicTacToeGameRoom> {
        return repo.getRoomsOfUser(userId)
    }

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

    fun joinRoom(playerId: String, roomId: String): Either<TicTacToeJoinRoomResponseError, Unit> {
        val room = repo.getRoom(roomId) ?: return Either.Left(TicTacToeJoinRoomResponseError.ROOM_NOT_FOUND)

        if(room.players.map { it.id }.contains(playerId)) {
            return Either.Left(TicTacToeJoinRoomResponseError.PLAYER_ALREADY_IN_ROOM)
        }

        if(room.opponentPlayer != null) {
            return Either.Left(TicTacToeJoinRoomResponseError.ROOM_ALREADY_FULL)
        }

        repo.storeRoom(
            room.copy(
                opponentPlayer = TicTacToePlayer(
                    id = playerId,
                    sign = room.hostPlayer.sign.otherSign(),
                ),
                roomState = TicTacToeGameRoom.State.InProgress,
                lastUpdate = Clock.System.now(),
            )
        )

        return Either.Right(Unit)
    }

    fun makeMove(playerId: String, roomId: String, cell: TicTacToeBoardCell): Either<TicTacToeMakeMoveResponseError, Unit> {
        val room = repo.getRoom(roomId) ?: return Either.Left(TicTacToeMakeMoveResponseError.ROOM_NOT_FOUND)

        val player = room.players.singleOrNull { it.id == playerId }

        if(player == null) {
            return Either.Left(TicTacToeMakeMoveResponseError.PLAYER_NOT_IN_ROOM)
        }
        if(room.currentPlayerSign != player.sign) {
            return Either.Left(TicTacToeMakeMoveResponseError.NOT_YOUR_TURN)
        }
        if(room.roomState !is TicTacToeGameRoom.State.InProgress) {
            return Either.Left(TicTacToeMakeMoveResponseError.GAME_NOT_IN_PROGRESS)
        }

        repo.storeRoom(
            room.copy(
                gameState = room.gameState + (cell to player.sign),
                currentPlayerSign = player.sign.otherSign(),
                lastUpdate = Clock.System.now(),
            ).updateWinner()
        )

        return Either.Right(Unit)
    }

    fun restartGame(playerId: String, roomId: String): Either<TicTacToeRestartGameResponseError, Unit> {
        val room = repo.getRoom(roomId) ?: return Either.Left(TicTacToeRestartGameResponseError.ROOM_NOT_FOUND)

        val roomState = room.roomState
        if(roomState !is TicTacToeGameRoom.State.Finished) {
            return Either.Left(TicTacToeRestartGameResponseError.GAME_NOT_FINISHED)
        }
        if(playerId != room.hostPlayer.id) {
            return Either.Left(TicTacToeRestartGameResponseError.NOT_THE_HOST)
        }

        repo.storeRoom(
            room.copy(
                gameState = emptyMap(),
                roomState = TicTacToeGameRoom.State.InProgress,
                pastGamesWinners = room.pastGamesWinners + roomState.winner,
                lastUpdate = Clock.System.now(),
            )
        )

        return Either.Right(Unit)
    }

    fun close(playerId: String, roomId: String) {
        repo.updateRoom(roomId) {
            val room = it ?: throw IllegalStateException("Room not found")

            val player = room.players.singleOrNull { it.id == playerId }
            require(player != null) { "Player not found" }

            room.copy(
                roomState = TicTacToeGameRoom.State.Closed(player),
                lastUpdate = Clock.System.now(),
            )
        }
    }
}