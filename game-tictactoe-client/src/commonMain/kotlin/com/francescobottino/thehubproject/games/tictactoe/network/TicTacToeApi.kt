package com.francescobottino.thehubproject.games.tictactoe.network

import arrow.core.Either
import com.francescobottino.thehubproject.config.PlatformConfig
import com.francescobottino.thehubproject.games.tictactoe.model.*
import com.francescobottino.thehubproject.mainJson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

class TicTacToeApi(
    private val client: HttpClient
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
    @OptIn(ExperimentalCoroutinesApi::class)
    fun joinRoomWebSocket(roomId: String): Flow<TicTacToeGameRoom> {
        return flow {
            val ws = client.webSocketSession("${PlatformConfig.wsUrl}/games/tictactoe/room/$roomId/updates")

            for (frame in ws.incoming) {
                if (frame is Frame.Text) {
                    emit(frame.readText())
                } else if (frame is Frame.Close) {
                    throw ClosedReceiveChannelException("Connection closed by server")
                }
            }
        }.mapNotNull { runCatching { mainJson.decodeFromString<TicTacToeGameRoom>(it) }.getOrNull() }
    }
}