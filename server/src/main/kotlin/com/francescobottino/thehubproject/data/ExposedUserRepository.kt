package com.francescobottino.thehubproject.data

import com.francescobottino.thehubproject.model.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedUserRepository(private val database: Database): UserRepository {
    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    private fun ResultRow.toUser(): User = User(
        id = this[UsersTable.id],
        username = this[UsersTable.username],
        passwordHash = this[UsersTable.passwordHash]
    )

    override suspend fun create(user: User): User? = newSuspendedTransaction(Dispatchers.IO, db = database) {
        UsersTable.insert {
            it[id] = user.id
            it[username] = user.username
            it[passwordHash] = user.passwordHash
        }.resultedValues?.singleOrNull()?.toUser()
    }

    override suspend fun findByUsername(username: String): User? =
        newSuspendedTransaction(Dispatchers.IO, db = database) {
            UsersTable.selectAll()
                .where { UsersTable.username eq username }
                .map { it.toUser() }
                .singleOrNull()
        }

    override suspend fun findById(id: String): User? = newSuspendedTransaction(Dispatchers.IO, db = database) {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }
}