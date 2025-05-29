package com.francescobottino.thehubproject.data

import org.jetbrains.exposed.sql.Table

object UsersTable: Table("users") { // Table name in PostgreSQL
    val id = varchar("id", 36) // UUID length
    val username = varchar("username", 256).uniqueIndex()
    val passwordHash = varchar("password_hash", 256) // BCrypt hashes can be long

    override val primaryKey = PrimaryKey(id)
}

