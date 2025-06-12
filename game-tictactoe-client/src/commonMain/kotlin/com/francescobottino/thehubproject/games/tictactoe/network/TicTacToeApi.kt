package com.francescobottino.thehubproject.games.tictactoe.network

import arrow.core.Either
import com.francescobottino.thehubproject.games.tictactoe.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TicTacToeApi(
    private val client: HttpClient,
    private val wsUrl: String,
) {
    suspend fun myRooms(): List<TicTacToeGameRoom> {
        return client.get("/games/tictactoe/my-rooms") {
            contentType(ContentType.Application.Json)
        }.body()
    }
    suspend fun makeRoom(request: TicTacToeMakeRoomRequest): String {
        return client.post("/games/tictactoe/room/make") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    suspend fun joinRoom(roomId: String): Either<TicTacToeJoinRoomResponseError, Unit> {
        return client.post("/games/tictactoe/room/$roomId/join") {
            contentType(ContentType.Application.Json)
        }.let {
            when {
                it.status.isSuccess() -> Either.Right(Unit)
                else -> Either.Left(it.body<TicTacToeJoinRoomResponseError>())
            }
        }
    }
    suspend fun makeMove(roomId: String, request: TicTacToeMakeMoveRequest): Either<TicTacToeMakeMoveResponseError, Unit> {
        return client.post("/games/tictactoe/room/$roomId/move") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.let {
            when {
                it.status.isSuccess() -> Either.Right(Unit)
                else -> Either.Left(it.body<TicTacToeMakeMoveResponseError>())
            }
        }
    }
    suspend fun restart(roomId: String): Either<TicTacToeRestartGameResponseError, Unit> {
        return client.post("/games/tictactoe/room/$roomId/restart") {
            contentType(ContentType.Application.Json)
        }.let {
            when {
                it.status.isSuccess() -> Either.Right(Unit)
                else -> Either.Left(it.body<TicTacToeRestartGameResponseError>())
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun joinRoomWebSocket(roomId: String): DefaultClientWebSocketSession {
        return client.webSocketSession("$wsUrl/games/tictactoe/room/$roomId/updates")
    }
}