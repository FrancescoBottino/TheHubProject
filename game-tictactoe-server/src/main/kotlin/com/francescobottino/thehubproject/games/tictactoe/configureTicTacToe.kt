package com.francescobottino.thehubproject.games.tictactoe

import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeMakeMoveRequest
import com.francescobottino.thehubproject.games.tictactoe.model.TicTacToeMakeRoomRequest
import com.francescobottino.thehubproject.getAuthUserId
import com.francescobottino.thehubproject.log
import com.francescobottino.thehubproject.mainJson
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
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.subDI

fun Route.configureTicTacToe() {
    route("tictactoe") {
        subDI {
            bindSingleton<TicTacToeGameRoomRepository> { TicTacToeInMemoryGameRoomRepository() } //todo use proper db
            bindSingleton { TicTacToeGameModule(instance()) }
        }

        authenticate("auth-jwt") {
            get("my-rooms") { myRooms() }
            route("room") {
                post("make") { makeRoom() }

                route("{roomId}") {
                    post("join") { joinRoom() }
                    post("move") { makeMove() }
                    webSocket("updates") { getUpdates() }
                }
            }
        }
    }
}

private suspend fun RoutingContext.myRooms() {
    val module by closestDI().instance<TicTacToeGameModule>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    try {
        val roomIds = module.getMyRooms(userId = userId)
        call.respond(HttpStatusCode.OK, message = roomIds)
    } catch (e: Exception) {
        call.application.log.error("myRooms failed", e)
        call.respond(HttpStatusCode.InternalServerError, message = e.message ?: "Unknown error occurred")
    }
}

private suspend fun RoutingContext.makeRoom() {
    val module by closestDI().instance<TicTacToeGameModule>()

    val userId = call.getAuthUserId() ?: run {
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
        val roomId = module.makeRoom(
            playerId = userId,
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
    val module by closestDI().instance<TicTacToeGameModule>()

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
        log.debug("user $userId is attempting to join room $roomId")

        val result = module.joinRoom(
            playerId = userId,
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

//todo handle errors
private suspend fun RoutingContext.makeMove() {
    val module by closestDI().instance<TicTacToeGameModule>()

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
        val result = module.makeMove(
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

context(route: Route)
private suspend fun DefaultWebSocketServerSession.getUpdates() {
    val repo by route.closestDI().instance<TicTacToeGameRoomRepository>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }
    val roomId = call.parameters["roomId"]!!

    log.debug("User $userId is connected to updates")

    val room = repo.getRoom(roomId)
    if (room == null) {
        log.debug("room $roomId does not exist")
        call.respond(HttpStatusCode.BadRequest, "Invalid request, room does not exist")
        return
    }

    if (!room.players.map { it.id }.contains(userId)) {
        log.debug("player is not in room")
        call.respond(HttpStatusCode.BadRequest, "Invalid request, player is not in room")
        return
    }

    log.debug("updating room with player connection")
    repo.updateRoom(roomId) { roomUpdate ->
        roomUpdate?.copy(connectedPlayerIds = roomUpdate.connectedPlayerIds + userId)
    }

    launch {
        for(frame in incoming) { /*nothing*/ }

        log.debug("Client closed the connection")
        log.debug("updating room with player disconnection")
        repo.updateRoom(roomId) { roomUpdate ->
            roomUpdate?.copy(connectedPlayerIds = roomUpdate.connectedPlayerIds - userId)
        }
        close(CloseReason(CloseReason.Codes.NORMAL, "Client closed the connection"))

        log.debug("stopped reading")
    }

    runCatching {
        log.debug("collecting room updates for player $userId in room $roomId")
        repo.getRoomUpdates(roomId).collect {
            log.debug("on room update for player $userId in room $roomId, sending update to client")
            send(Frame.Text(mainJson.encodeToString(it)))
        }
    }.onFailure {
        log.debug("error collecting updates: $it | ${it.message} | ${it.stackTraceToString()}")
        log.debug("updating room with player disconnection")
        repo.updateRoom(roomId) { roomUpdate ->
            roomUpdate?.copy(connectedPlayerIds = roomUpdate.connectedPlayerIds - userId)
        }
        close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Error relaying updates"))
    }
}