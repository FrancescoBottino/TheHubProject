package com.francescobottino.thehubproject.data

import org.jetbrains.exposed.sql.Table

object UsersTable: Table("users") {
    val id = varchar("id", 36)
    val username = varchar("username", 256).uniqueIndex()
    val passwordHash = varchar("password_hash", 256)

    override val primaryKey = PrimaryKey(id)
}

