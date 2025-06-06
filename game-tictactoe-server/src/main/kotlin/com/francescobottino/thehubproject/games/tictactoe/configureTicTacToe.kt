package com.francescobottino.thehubproject.games.tictactoe

import com.francescobottino.thehubproject.games.tictactoe.api.TicTacToeMakeMoveRequest
import com.francescobottino.thehubproject.games.tictactoe.api.TicTacToeMakeRoomRequest
import com.francescobottino.thehubproject.getAuthUserId
import com.francescobottino.thehubproject.log
import com.francescobottino.thehubproject.mainJson
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
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

        route("room") {
            authenticate("auth-jwt") {
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

private suspend fun RoutingContext.makeRoom() {
    val module by closestDI().instance<TicTacToeGameModule>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    try {
        val request = call.receive<TicTacToeMakeRoomRequest>()
        val roomId = module.makeRoom(
            playerId = userId,
            chosenSign = request.chosenSign,
            startingSign = request.startingSign,
        )
        call.respond(HttpStatusCode.OK, message = roomId)
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, message = e.message ?: "Invalid request")
    }
}

private suspend fun RoutingContext.joinRoom() {
    val module by closestDI().instance<TicTacToeGameModule>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    try {
        val roomId = call.parameters.getOrFail<String>("roomId")
        module.joinRoom(
            playerId = userId,
            roomId = roomId,
        )
        call.respond(HttpStatusCode.OK)
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, message = e.message ?: "Invalid request")
    }
}

private suspend fun RoutingContext.makeMove() {
    val module by closestDI().instance<TicTacToeGameModule>()

    val userId = call.getAuthUserId() ?: run {
        call.respond(HttpStatusCode.Unauthorized, "User not found or invalid token")
        return
    }

    try {
        val roomId = call.parameters.getOrFail<String>("roomId")
        val request = call.receive<TicTacToeMakeMoveRequest>()
        module.makeMove(
            playerId = userId,
            roomId = roomId,
            cell = request.cell,
        )
        call.respond(HttpStatusCode.OK)
    } catch (e: Exception) {
        call.respond(HttpStatusCode.BadRequest, message = e.message ?: "Invalid request")
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

    log.debug("notifying player connection")
    repo.updateRoom(roomId) { roomUpdate ->
        roomUpdate?.copy(connectedPlayerIds = roomUpdate.connectedPlayerIds + userId)
    }

    runCatching {
        repo.getRoomUpdates(roomId).collect {
            send(Frame.Text(mainJson.encodeToString(it)))
        }
    }.onFailure {
        log.debug("error collecting updates: $it | ${it.message} | ${it.stackTraceToString()}")
        log.debug("notifying player disconnection")
        repo.updateRoom(roomId) { roomUpdate ->
            roomUpdate?.copy(connectedPlayerIds = roomUpdate.connectedPlayerIds - userId)
        }
    }
}