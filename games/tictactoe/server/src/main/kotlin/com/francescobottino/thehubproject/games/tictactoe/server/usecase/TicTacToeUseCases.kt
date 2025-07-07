package com.francescobottino.thehubproject.games.tictactoe.server.usecase

import arrow.core.Either
import com.francescobottino.thehubproject.games.tictactoe.server.repository.TicTacToeGameRoomRepository
import com.francescobottino.thehubproject.games.tictactoe.server.repository.UpdateResult
import com.francescobottino.thehubproject.games.tictactoe.server.repository.updateRoomOrSkip
import com.francescobottino.thehubproject.games.tictactoe.server.repository.write
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeBoardCell
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayer
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeCloseGameResponseError
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeJoinRoomResponseError
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeMakeMoveResponseError
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeRestartGameResponseError
import com.francescobottino.thehubproject.shared.model.PaginatedResponse
import com.francescobottino.thehubproject.shared.model.PaginationParams
import com.francescobottino.thehubproject.shared.model.UserResponse
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TicTacToeUseCases(
    private val repo: TicTacToeGameRoomRepository,
) {
    fun getMyRooms(userId: String, paginationParams: PaginationParams): PaginatedResponse<TicTacToeGameRoom> {
        return repo.getRoomsOfUser(userId, paginationParams)
    }

    fun makeRoom(player: UserResponse, chosenSign: TicTacToePlayerSign, startingSign: TicTacToePlayerSign): String {
        val room = TicTacToeGameRoom(
            id = Uuid.Companion.random().toString(),
            hostPlayer = TicTacToePlayer(
                user = player,
                sign = chosenSign,
            ),
            currentPlayerSign = startingSign
        )

        repo.write(room)

        return room.id
    }

    fun joinRoom(player: UserResponse, roomId: String): Either<TicTacToeJoinRoomResponseError, Unit> {
        var validationError: TicTacToeJoinRoomResponseError? = null

        repo.updateRoomOrSkip(roomId) { room ->
            if(room == null) {
                validationError = TicTacToeJoinRoomResponseError.ROOM_NOT_FOUND
                return@updateRoomOrSkip null
            }

            if(room.players.map { it.user.id }.contains(player.id)) {
                validationError = TicTacToeJoinRoomResponseError.PLAYER_ALREADY_IN_ROOM
                return@updateRoomOrSkip null
            }

            if(room.opponentPlayer != null) {
                validationError = TicTacToeJoinRoomResponseError.ROOM_ALREADY_FULL
                return@updateRoomOrSkip null
            }

            room.copy(
                opponentPlayer = TicTacToePlayer(
                    player,
                    sign = room.hostPlayer.sign.otherSign(),
                ),
                roomState = TicTacToeGameRoom.State.InProgress,
                lastUpdate = Clock.System.now(),
            )
        }

        if(validationError != null) {
            return Either.Left(validationError)
        }

        return Either.Right(Unit)
    }

    fun makeMove(playerId: String, roomId: String, cell: TicTacToeBoardCell): Either<TicTacToeMakeMoveResponseError, Unit> {
        var validationError: TicTacToeMakeMoveResponseError? = null

        repo.updateRoomOrSkip(roomId) { room ->
            if(room == null) {
                validationError = TicTacToeMakeMoveResponseError.ROOM_NOT_FOUND
                return@updateRoomOrSkip null
            }

            val player = room.players.singleOrNull { it.user.id == playerId }

            if(player == null) {
                validationError = TicTacToeMakeMoveResponseError.PLAYER_NOT_IN_ROOM
                return@updateRoomOrSkip null
            }
            if(room.currentPlayerSign != player.sign) {
                validationError = TicTacToeMakeMoveResponseError.NOT_YOUR_TURN
                return@updateRoomOrSkip null
            }
            if(room.roomState !is TicTacToeGameRoom.State.InProgress) {
                validationError = TicTacToeMakeMoveResponseError.GAME_NOT_IN_PROGRESS
                return@updateRoomOrSkip null
            }
            if(room.gameState.containsKey(cell)) {
                validationError = TicTacToeMakeMoveResponseError.CELL_ALREADY_OCCUPIED
                return@updateRoomOrSkip null
            }

            room.copy(
                gameState = room.gameState + (cell to player.sign),
                currentPlayerSign = player.sign.otherSign(),
                lastUpdate = Clock.System.now(),
            ).updateWinner()
        }

        if(validationError != null) {
            return Either.Left(validationError)
        }

        return Either.Right(Unit)
    }

    fun restartGame(playerId: String, roomId: String): Either<TicTacToeRestartGameResponseError, Unit> {
        var validationError: TicTacToeRestartGameResponseError? = null

        repo.updateRoomOrSkip(roomId) { room ->
            if(room == null) {
                validationError = TicTacToeRestartGameResponseError.ROOM_NOT_FOUND
                return@updateRoomOrSkip null
            }

            val roomState = room.roomState
            if(roomState !is TicTacToeGameRoom.State.Finished) {
                validationError = TicTacToeRestartGameResponseError.GAME_NOT_FINISHED
                return@updateRoomOrSkip null
            }

            if(playerId != room.hostPlayer.user.id) {
                validationError = TicTacToeRestartGameResponseError.NOT_THE_HOST
                return@updateRoomOrSkip null
            }

            room.copy(
                gameState = emptyMap(),
                roomState = TicTacToeGameRoom.State.InProgress,
                pastGamesWinners = room.pastGamesWinners + roomState.winner,
                lastUpdate = Clock.System.now(),
            )
        }

        if(validationError != null) {
            return Either.Left(validationError)
        }

        return Either.Right(Unit)
    }

    fun close(playerId: String, roomId: String): Either<TicTacToeCloseGameResponseError, Unit> {
        var validationError: TicTacToeCloseGameResponseError? = null

        repo.updateRoom(roomId) { room ->
            if(room == null) {
                validationError = TicTacToeCloseGameResponseError.ROOM_NOT_FOUND
                return@updateRoom UpdateResult.Skip()
            }

            if(room.roomState is TicTacToeGameRoom.State.Closed) {
                validationError = TicTacToeCloseGameResponseError.ROOM_ALREADY_CLOSED
                return@updateRoom UpdateResult.Skip()
            }

            val player = room.players.singleOrNull { it.user.id == playerId }

            if(player == null) {
                validationError = TicTacToeCloseGameResponseError.PLAYER_NOT_IN_ROOM
                return@updateRoom UpdateResult.Skip()
            }

            val roomState = room.roomState

            when(roomState) {
                is TicTacToeGameRoom.State.WaitingForOpponent -> {
                    UpdateResult.Delete()
                }
                else -> {
                    UpdateResult.Write(
                        room.let {
                            if(roomState is TicTacToeGameRoom.State.Finished) {
                                it.copy(
                                    gameState = emptyMap(),
                                    pastGamesWinners = room.pastGamesWinners + roomState.winner,
                                )
                            } else {
                                it
                            }
                        }.copy(
                            roomState = TicTacToeGameRoom.State.Closed(player.sign),
                            lastUpdate = Clock.System.now(),
                        )
                    )
                }
            }
        }

        if(validationError != null) {
            return Either.Left(validationError)
        }

        return Either.Right(Unit)
    }
}