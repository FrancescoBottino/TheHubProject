package com.francescobottino.thehubproject.games.tictactoe.server.repository

import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.currentPlayerSign
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.gameStateJson
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.hostPlayerId
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.hostPlayerSign
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.lastUpdate
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.opponentPlayerId
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.opponentPlayerSign
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.pastGamesWinnersJson
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.roomState
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.roomStateClosedBySign
import com.francescobottino.thehubproject.games.tictactoe.server.tables.TicTacToeGameRoomTable.roomStateWinnerSign
import com.francescobottino.thehubproject.games.tictactoe.shared.model.*
import com.francescobottino.thehubproject.server_shared.data.UsersTable
import com.francescobottino.thehubproject.shared.mainJson
import com.francescobottino.thehubproject.shared.model.PaginatedResponse
import com.francescobottino.thehubproject.shared.model.PaginationInfo
import com.francescobottino.thehubproject.shared.model.PaginationParams
import com.francescobottino.thehubproject.shared.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.ConcurrentHashMap

class TicTacToeGameRoomRepository(private val database: Database) {
    // In-memory storage for active connections and room updates
    private val activeConnections = ConcurrentHashMap<String, List<String>>() // roomId -> List of connected user IDs
    private val roomUpdateFlows = ConcurrentHashMap<String, MutableStateFlow<TicTacToeGameRoom?>>()

    init {
        transaction(database) {
            SchemaUtils.create(TicTacToeGameRoomTable)
        }
    }

    fun getRoom(roomId: String): TicTacToeGameRoom? {
        return transaction(database) {
            TicTacToeGameRoomTable.selectAll()
                .where { TicTacToeGameRoomTable.id eq roomId }
                .singleOrNull()
                ?.let { row -> reconstructRoom(row) }
        }
    }

    fun updateRoom(roomId: String, updater: (TicTacToeGameRoom?) -> UpdateResult<TicTacToeGameRoom>) {
        return transaction(database) {
            val currentRoom = TicTacToeGameRoomTable.selectAll()
                .where { TicTacToeGameRoomTable.id eq roomId }
                .forUpdate()
                .singleOrNull()
                ?.let { row -> reconstructRoom(row) }

            val updateResult = updater(currentRoom)

            when(updateResult) {
                is UpdateResult.Delete -> {
                    deleteRoomInternal(roomId)

                    activeConnections.remove(roomId)
                    roomUpdateFlows.remove(roomId)
                }
                is UpdateResult.Write<TicTacToeGameRoom> -> {
                    val updatedRoom = updateResult.data
                    if(currentRoom == null) {
                        insertRoomInternal(updatedRoom)
                    } else {
                        updateRoomInternal(updatedRoom)
                    }

                    activeConnections[roomId] = updatedRoom.connectedPlayerIds
                    roomUpdateFlows[roomId]?.value = updatedRoom
                }
                is UpdateResult.Skip -> {}
            }
        }
    }

    fun getRoomUpdates(roomId: String): Flow<TicTacToeGameRoom> {
        return roomUpdateFlows
            .getOrPut(roomId) { MutableStateFlow(getRoom(roomId)) }
            .asStateFlow()
            .filterNotNull()
    }

    fun deleteRoom(roomId: String) {
        transaction(database) {
            TicTacToeGameRoomTable.selectAll()
                .where { TicTacToeGameRoomTable.id eq id }
                .forUpdate()
                .singleOrNull()

            deleteRoomInternal(roomId)
        }
        activeConnections.remove(roomId)
        roomUpdateFlows.remove(roomId)
    }

    fun getRoomsOfUser(userId: String, paginationParams: PaginationParams): PaginatedResponse<TicTacToeGameRoom> {
        return transaction(database) {
            val query = TicTacToeGameRoomTable.selectAll()
                .where { (hostPlayerId eq userId) or (opponentPlayerId eq userId) }

            val totalItems = query.count().toInt()

            val items = query
                .orderBy(lastUpdate, SortOrder.DESC)
                .limit(paginationParams.limit, paginationParams.offset.toLong())
                .mapNotNull { row -> reconstructRoom(row) }

            val totalPages = (totalItems + paginationParams.limit - 1) / paginationParams.limit

            PaginatedResponse(
                items,
                pagination = PaginationInfo(
                    currentPage = paginationParams.page,
                    totalPages = totalPages,
                    totalItems = totalItems,
                    hasNext = paginationParams.page < totalPages,
                    hasPrevious = paginationParams.page > 1
                )
            )
        }
    }

    private fun Transaction.updateRoomInternal(room: TicTacToeGameRoom) {
        TicTacToeGameRoomTable.update({ TicTacToeGameRoomTable.id eq room.id }) {
            prepareStatementForRoom(it, room)
        }
    }

    private fun Transaction.insertRoomInternal(room: TicTacToeGameRoom) {
        TicTacToeGameRoomTable.insert {
            it[id] = room.id
            prepareStatementForRoom(it, room)
        }
    }

    private fun Transaction.deleteRoomInternal(roomId: String) {
        TicTacToeGameRoomTable.deleteWhere { TicTacToeGameRoomTable.id eq roomId }
    }

    private fun getUser(userId: String): UserResponse? {
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

    private fun prepareStatementForRoom(statement: UpdateBuilder<Int>, room: TicTacToeGameRoom) {
        statement[hostPlayerId] = room.hostPlayer.user.id
        statement[hostPlayerSign] = room.hostPlayer.sign
        statement[opponentPlayerId] = room.opponentPlayer?.user?.id
        statement[opponentPlayerSign] = room.opponentPlayer?.sign
        statement[gameStateJson] = mainJson.encodeToString(room.gameState.toSerializableMap())
        statement[pastGamesWinnersJson] = mainJson.encodeToString(room.pastGamesWinners)
        statement[currentPlayerSign] = room.currentPlayerSign
        val roomStateField = room.roomState
        statement[roomState] = when (roomStateField) {
            is TicTacToeGameRoom.State.WaitingForOpponent -> "WaitingForOpponent"
            is TicTacToeGameRoom.State.InProgress -> "InProgress"
            is TicTacToeGameRoom.State.Finished -> "Finished"
            is TicTacToeGameRoom.State.Closed -> "Closed"
        }
        statement[roomStateWinnerSign] = when (roomStateField) {
            is TicTacToeGameRoom.State.Finished -> roomStateField.winner
            else -> null
        }
        statement[roomStateClosedBySign] = when (roomStateField) {
            is TicTacToeGameRoom.State.Closed -> roomStateField.byPlayer
            else -> null
        }
        statement[lastUpdate] = room.lastUpdate.toEpochMilliseconds()
    }

    private fun reconstructRoom(row: ResultRow): TicTacToeGameRoom? {
        val hostUser = getUser(row[hostPlayerId]) ?: return null
        val opponentUser = row[opponentPlayerId]?.let { getUser(it) }

        val hostPlayer = TicTacToePlayer(hostUser, row[hostPlayerSign])
        val opponentPlayer = opponentUser?.let { user ->
            row[opponentPlayerSign]?.let { sign ->
                TicTacToePlayer(user, sign)
            }
        }

        val gameState = mainJson.decodeFromString<Map<String, String>>(row[gameStateJson])
            .toGameState()

        val pastGamesWinners = mainJson.decodeFromString<List<TicTacToePlayerSign?>>(row[pastGamesWinnersJson])

        val roomState = when (row[roomState]) {
            "WaitingForOpponent" -> TicTacToeGameRoom.State.WaitingForOpponent
            "InProgress" -> TicTacToeGameRoom.State.InProgress
            "Finished" -> TicTacToeGameRoom.State.Finished(row[roomStateWinnerSign])
            "Closed" -> TicTacToeGameRoom.State.Closed(row[roomStateClosedBySign]!!)
            else -> TicTacToeGameRoom.State.WaitingForOpponent
        }

        val connectedPlayerIds = activeConnections[row[TicTacToeGameRoomTable.id]] ?: emptyList()

        return TicTacToeGameRoom(
            id = row[TicTacToeGameRoomTable.id],
            hostPlayer = hostPlayer,
            opponentPlayer = opponentPlayer,
            gameState = gameState,
            pastGamesWinners = pastGamesWinners,
            currentPlayerSign = row[currentPlayerSign],
            roomState = roomState,
            connectedPlayerIds = connectedPlayerIds,
            lastUpdate = Instant.fromEpochMilliseconds(row[lastUpdate])
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