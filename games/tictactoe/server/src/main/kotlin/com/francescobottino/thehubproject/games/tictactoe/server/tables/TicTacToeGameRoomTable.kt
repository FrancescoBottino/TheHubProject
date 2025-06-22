package com.francescobottino.thehubproject.games.tictactoe.server.tables

import com.francescobottino.thehubproject.games.tictactoe.shared.model.TicTacToePlayerSign
import com.francescobottino.thehubproject.server_shared.data.UsersTable
import org.jetbrains.exposed.sql.Table

object TicTacToeGameRoomTable: Table("tic_tac_toe_game_rooms") {
    val id = varchar("id", 36)
    val hostPlayerId = varchar("host_player_id", 36).references(UsersTable.id)
    val hostPlayerSign = enumerationByName("host_player_sign", 1, TicTacToePlayerSign::class)
    val opponentPlayerId = varchar("opponent_player_id", 36).references(UsersTable.id).nullable()
    val opponentPlayerSign = enumerationByName("opponent_player_sign", 1, TicTacToePlayerSign::class).nullable()
    val gameStateJson = text("game_state_json") // JSON representation of the game state
    val pastGamesWinnersJson = text("past_games_winners_json") // JSON array of past winners
    val currentPlayerSign = enumerationByName("current_player_sign", 1, TicTacToePlayerSign::class)
    val roomState = varchar("room_state", 50) // Serialized state type
    val roomStateWinnerId = varchar("room_state_winner_id", 36).nullable() // For Finished state
    val roomStateClosedById = varchar("room_state_closed_by_id", 36).nullable() // For Closed state
    val lastUpdate = long("last_update") // Instant as epoch milliseconds

    override val primaryKey = PrimaryKey(id)
}