package com.francescobottino.thehubproject.games.tictactoe.server.repository

sealed interface UpdateResult<T> {
    class Skip<T>(): UpdateResult<T>
    class Delete<T>(): UpdateResult<T>
    data class Write<T>(val data: T): UpdateResult<T>
}