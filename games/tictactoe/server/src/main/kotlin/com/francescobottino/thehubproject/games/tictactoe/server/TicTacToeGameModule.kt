package com.francescobottino.thehubproject.games.tictactoe.server

import com.francescobottino.thehubproject.games.tictactoe.server.di.ticTacToeModule
import com.francescobottino.thehubproject.games.tictactoe.server.repository.TicTacToeGameRoomRepository
import com.francescobottino.thehubproject.games.tictactoe.server.repository.updateRoomOrSkip
import com.francescobottino.thehubproject.games.tictactoe.server.usecase.TicTacToeUseCases
import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToeGameRoom
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeMakeMoveRequest
import com.francescobottino.thehubproject.games.tictactoe.shared.model.api.TicTacToeMakeRoomRequest
import com.francescobottino.thehubproject.server_features.core.*
import com.francescobottino.thehubproject.server_features.core.auth.AUTH_JWT
import com.francescobottino.thehubproject.server_features.core.model.safe
import com.francescobottino.thehubproject.shared_features.core.mainJson
import com.francescobottino.thehubproject.shared_features.core.model.PaginationParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.ktor.ext.inject

object TicTacToeGameModule: GameModule {
    override fun Route.configure() {
        route("tictactoe") {
            loadKoinModules(ticTacToeModule)

            authenticate(AUTH_JWT) {
                get("my-rooms") { myRooms() }
            }
            route("room") {
                authenticate(AUTH_JWT) {
                    post("make") { makeRoom() }
                }
                route("{roomId}") {
                    authenticate(AUTH_JWT) {
                        post("join") { joinRoom() }
                        post("move") { makeMove() }
                        post("restart") { restart() }
                        post("close") { close() }
                    }
                    webSocket("updates") { getUpdates() }
                }
            }
        }
    }
}

private suspend fun RoutingContext.myRooms() {
    val useCase by call.inject<TicTacToeUseCases>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    val paginationParams = PaginationParams(
        page = call.parameters.getOrFail<Int>("page"),
        limit = call.parameters.getOrFail<Int>("limit"),
    )

    try {
        val paginatedRooms = useCase.getMyRooms(userId = userId, paginationParams = paginationParams)
        call.respond(HttpStatusCode.OK, message = paginatedRooms)
    } catch (e: Exception) {
        call.application.log.error("myRooms failed", e)
        call.respond(HttpStatusCode.InternalServerError, message = e.message ?: "Unknown error occurred")
    }
}

private suspend fun RoutingContext.makeRoom() {
    val useCase by call.inject<TicTacToeUseCases>()

    val user = call.getAuthUser()?.safe() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    val request = try {
        call.receive<TicTacToeMakeRoomRequest>()
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    try {
        val roomId = useCase.makeRoom(
            player = user,
            chosenSign = request.chosenSign,
            startingSign = request.startingSign,
        )
        call.respond(HttpStatusCode.OK, message = roomId)
    } catch (e: Exception) {
        call.application.log.error("makeRoom failed", e)
        call.respond(HttpStatusCode.InternalServerError, message = e.message ?: "Unknown error occurred")
    }
}

private suspend fun RoutingContext.joinRoom() {
    val useCase by call.inject<TicTacToeUseCases>()

    val user = call.getAuthUser()?.safe() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    val roomId = try {
        call.parameters.getOrFail<String>("roomId")
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    try {
        log.debug("user $user is attempting to join room $roomId")

        val result = useCase.joinRoom(
            player = user,
            roomId = roomId,
        )

        log.debug("result $result")

        result.onRight {
            call.respond(HttpStatusCode.OK)
        }.onLeft {
            call.respond(HttpStatusCode.BadRequest, it)
        }
    } catch (e: Exception) {
        call.application.log.error("joinRoom failed", e)
        call.respond(HttpStatusCode.InternalServerError, message = e.message ?: "Unknown error occurred")
    }
}

private suspend fun RoutingContext.makeMove() {
    val useCase by call.inject<TicTacToeUseCases>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    val roomId = try {
        call.parameters.getOrFail<String>("roomId")
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    val request = try {
        call.receive<TicTacToeMakeMoveRequest>()
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    try {
        val result = useCase.makeMove(
            playerId = userId,
            roomId = roomId,
            cell = request.cell,
        )

        result.onRight {
            call.respond(HttpStatusCode.OK)
        }.onLeft {
            call.respond(HttpStatusCode.BadRequest, it)
        }
    } catch (e: Exception) {
        call.application.log.error("makeMove failed", e)
        call.respond(HttpStatusCode.InternalServerError, message = e.message ?: "Invalid request")
    }
}

private suspend fun RoutingContext.restart() {
    val useCase by call.inject<TicTacToeUseCases>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    val roomId = try {
        call.parameters.getOrFail<String>("roomId")
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    try {
        val result = useCase.restartGame(
            playerId = userId,
            roomId = roomId,
        )

        result.onRight {
            call.respond(HttpStatusCode.OK)
        }.onLeft {
            call.respond(HttpStatusCode.BadRequest, it)
        }
    } catch (e: Exception) {
        call.application.log.error("restart failed", e)
        call.respond(HttpStatusCode.InternalServerError, message = e.message ?: "Invalid request")
    }
}

private suspend fun RoutingContext.close() {
    val useCase by call.inject<TicTacToeUseCases>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    val roomId = try {
        call.parameters.getOrFail<String>("roomId")
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
        return
    }

    try {
        val result = useCase.close(
            playerId = userId,
            roomId = roomId,
        )

        result.onRight {
            call.respond(HttpStatusCode.OK)
        }.onLeft {
            call.respond(HttpStatusCode.BadRequest, it)
        }
    } catch (e: Exception) {
        call.application.log.error("restart failed", e)
        call.respond(HttpStatusCode.InternalServerError, message = e.message ?: "Invalid request")
    }
}

context(route: Route)
private suspend fun DefaultWebSocketServerSession.getUpdates() {
    val repo by call.inject<TicTacToeGameRoomRepository>()

    val userId = getWebsocketAuthUserId() ?: run {
        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "User not found or invalid token"))
        return
    }

    val roomId = call.parameters["roomId"]
    if (roomId == null) {
        log.debug("roomId not found")
        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid request, requires room id"))
        return
    }

    val room = repo.getRoom(roomId)
    if (room == null) {
        log.debug("room $roomId does not exist")
        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid request, room does not exist"))
        return
    }

    if (!room.players.map { it.user.id }.contains(userId)) {
        log.debug("player is not in room")
        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid request, player is not in room"))
        return
    }

    if(room.roomState is TicTacToeGameRoom.State.Closed) {
        close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Room is closed"))
    }

    log.debug("updating room with player connection")
    repo.updateRoomOrSkip(roomId) { room ->
        room?.let {
            room.copy(connectedPlayerIds = room.connectedPlayerIds + userId)
        }
    }

    launch {
        for(frame in incoming) { /*nothing*/ }

        log.debug("Client closed the connection")
        log.debug("updating room with player disconnection")
        repo.updateRoomOrSkip(roomId) { room ->
            room?.let {
                room.copy(connectedPlayerIds = room.connectedPlayerIds - userId)
            }
        }
        close(CloseReason(CloseReason.Codes.NORMAL, "Client closed the connection"))

        log.debug("stopped reading")
    }

    runCatching {
        log.debug("collecting room updates for player $userId in room $roomId")
        repo.getRoomUpdates(roomId).collect {
            log.debug("on room update for player $userId in room $roomId, sending update to client")
            send(Frame.Text(mainJson.encodeToString(it)))

            if(it.roomState is TicTacToeGameRoom.State.Closed) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Game has been closed"))
            }
        }
    }.onFailure {
        log.debug("error collecting updates: $it | ${it.message} | ${it.stackTraceToString()}")
        log.debug("updating room with player disconnection")
        repo.updateRoomOrSkip(roomId) { room ->
            room?.let {
                room.copy(connectedPlayerIds = room.connectedPlayerIds - userId)
            }
        }
        close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Error relaying updates"))
    }
}