package com.francescobottino.thehubproject.games.tictactoe.repository

import com.francescobottino.thehubproject.data.UsersTable
import com.francescobottino.thehubproject.data.upsert
import com.francescobottino.thehubproject.games.tictactoe.model.*
import com.francescobottino.thehubproject.games.tictactoe.tables.TicTacToeGameRoomTable
import com.francescobottino.thehubproject.mainJson
import com.francescobottino.thehubproject.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.ConcurrentHashMap

class TicTacToeExposedGameRoomRepository(private val database: Database) : TicTacToeGameRoomRepository {
    // In-memory storage for active connections and room updates
    private val activeConnections = ConcurrentHashMap<String, List<String>>() // roomId -> List of connected user IDs
    private val roomUpdateFlows = ConcurrentHashMap<String, MutableStateFlow<TicTacToeGameRoom?>>()

    init {
        transaction(database) {
            SchemaUtils.create(TicTacToeGameRoomTable)
        }
    }

    override fun storeRoom(room: TicTacToeGameRoom) {
        transaction(database) {
            // Store/update the room
            TicTacToeGameRoomTable.upsert(TicTacToeGameRoomTable.id) {
                it[id] = room.id
                it[hostPlayerId] = room.hostPlayer.user.id
                it[hostPlayerSign] = room.hostPlayer.sign
                it[opponentPlayerId] = room.opponentPlayer?.user?.id
                it[opponentPlayerSign] = room.opponentPlayer?.sign
                it[gameStateJson] = mainJson.encodeToString(room.gameState.toSerializableMap())
                it[pastGamesWinnersJson] = mainJson.encodeToString(room.pastGamesWinners.map { winner -> winner?.user?.id })
                it[currentPlayerSign] = room.currentPlayerSign
                val roomStateField = room.roomState
                it[roomState] = when (roomStateField) {
                    is TicTacToeGameRoom.State.WaitingForOpponent -> "WaitingForOpponent"
                    is TicTacToeGameRoom.State.InProgress -> "InProgress"
                    is TicTacToeGameRoom.State.Finished -> "Finished"
                    is TicTacToeGameRoom.State.Closed -> "Closed"
                }
                it[roomStateWinnerId] = when (roomStateField) {
                    is TicTacToeGameRoom.State.Finished -> roomStateField.winner?.user?.id
                    else -> null
                }
                it[roomStateClosedById] = when (roomStateField) {
                    is TicTacToeGameRoom.State.Closed -> roomStateField.byPlayer.user.id
                    else -> null
                }
                it[lastUpdate] = room.lastUpdate.toEpochMilliseconds()
            }.execute(this)

            // Update in-memory connections
            activeConnections[room.id] = room.connectedPlayerIds

            // Notify flow subscribers
            roomUpdateFlows[room.id]?.value = room
        }
    }

    override fun getRoom(id: String): TicTacToeGameRoom? {
        return transaction(database) {
            TicTacToeGameRoomTable.selectAll()
                .where { TicTacToeGameRoomTable.id eq id }
                .singleOrNull()
                ?.let { row -> reconstructRoom(row) }
        }
    }

    override fun getRoomUpdates(id: String): Flow<TicTacToeGameRoom> {
        return roomUpdateFlows
            .getOrPut(id) { MutableStateFlow(getRoom(id)) }
            .asStateFlow()
            .filterNotNull()
    }

    override fun deleteRoom(id: String) {
        transaction(database) {
            TicTacToeGameRoomTable.deleteWhere { TicTacToeGameRoomTable.id eq id }
        }

        // Clean up in-memory data
        activeConnections.remove(id)
        roomUpdateFlows.remove(id)
    }

    override fun getRoomsOfUser(userId: String): List<TicTacToeGameRoom> {
        return transaction(database) {
            TicTacToeGameRoomTable.selectAll()
                .where {
                    (TicTacToeGameRoomTable.hostPlayerId eq userId) or
                    (TicTacToeGameRoomTable.opponentPlayerId eq userId)
                }
                .mapNotNull { row -> reconstructRoom(row) }
        }
    }

    // Helper method to update connected players without full room update
    fun updateConnectedPlayers(roomId: String, connectedPlayerIds: List<String>) {
        activeConnections[roomId] = connectedPlayerIds

        // Update the flow with current room state + new connections
        getRoom(roomId)?.let { room ->
            val updatedRoom = room.copy(connectedPlayerIds = connectedPlayerIds)
            roomUpdateFlows[roomId]?.value = updatedRoom
        }
    }

    private fun getUserResponse(userId: String): UserResponse? {
        return UsersTable.selectAll()
            .where { UsersTable.id eq userId }
            .singleOrNull()
            ?.let { row ->
                UserResponse(
                    id = row[UsersTable.id],
                    username = row[UsersTable.username]
                )
            }
    }

    private fun reconstructRoom(row: ResultRow): TicTacToeGameRoom? {
        val hostUser = getUserResponse(row[TicTacToeGameRoomTable.hostPlayerId]) ?: return null
        val opponentUser = row[TicTacToeGameRoomTable.opponentPlayerId]?.let { getUserResponse(it) }

        val hostPlayer = TicTacToePlayer(hostUser, row[TicTacToeGameRoomTable.hostPlayerSign])
        val opponentPlayer = opponentUser?.let { user ->
            row[TicTacToeGameRoomTable.opponentPlayerSign]?.let { sign ->
                TicTacToePlayer(user, sign)
            }
        }

        val gameState = mainJson.decodeFromString<Map<String, String>>(row[TicTacToeGameRoomTable.gameStateJson])
            .toGameState()

        val pastWinnerIds = mainJson.decodeFromString<List<String?>>(row[TicTacToeGameRoomTable.pastGamesWinnersJson])
        val pastGamesWinners = pastWinnerIds.map { winnerId ->
            winnerId?.let { id ->
                if (id == hostPlayer.user.id) hostPlayer
                else opponentPlayer?.takeIf { it.user.id == id }
            }
        }

        val roomState = when (row[TicTacToeGameRoomTable.roomState]) {
            "WaitingForOpponent" -> TicTacToeGameRoom.State.WaitingForOpponent
            "InProgress" -> TicTacToeGameRoom.State.InProgress
            "Finished" -> {
                val winnerId = row[TicTacToeGameRoomTable.roomStateWinnerId]
                val winner = winnerId?.let { id ->
                    if (id == hostPlayer.user.id) hostPlayer
                    else opponentPlayer?.takeIf { it.user.id == id }
                }
                TicTacToeGameRoom.State.Finished(winner)
            }
            "Closed" -> {
                val closedById = row[TicTacToeGameRoomTable.roomStateClosedById]!!
                val byPlayer = if (closedById == hostPlayer.user.id) hostPlayer
                else opponentPlayer!!
                TicTacToeGameRoom.State.Closed(byPlayer)
            }
            else -> TicTacToeGameRoom.State.WaitingForOpponent
        }

        val connectedPlayerIds = activeConnections[row[TicTacToeGameRoomTable.id]] ?: emptyList()

        return TicTacToeGameRoom(
            id = row[TicTacToeGameRoomTable.id],
            hostPlayer = hostPlayer,
            opponentPlayer = opponentPlayer,
            gameState = gameState,
            pastGamesWinners = pastGamesWinners,
            currentPlayerSign = row[TicTacToeGameRoomTable.currentPlayerSign],
            roomState = roomState,
            connectedPlayerIds = connectedPlayerIds,
            lastUpdate = Instant.fromEpochMilliseconds(row[TicTacToeGameRoomTable.lastUpdate])
        )
    }

    // Helper extensions for serialization
    private fun TicTacToeGameState.toSerializableMap(): Map<String, String> {
        return this.mapKeys { "${it.key.r},${it.key.c}" }
            .mapValues { it.value.name }
    }

    private fun Map<String, String>.toGameState(): TicTacToeGameState {
        return this.mapKeys {
            val (r, c) = it.key.split(",").map { coord -> coord.toInt() }
            TicTacToeBoardCell(r, c)
        }.mapValues { TicTacToePlayerSign.valueOf(it.value) }
    }
}