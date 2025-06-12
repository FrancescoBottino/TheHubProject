package com.francescobottino.thehubproject.data

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.InsertStatement

class UpsertStatement<Key : Any>(
    table: Table,
    private val keys: Array<out Column<*>>,
): InsertStatement<Key>(table, false) {
    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String {
        val insertSql = super.prepareSQL(transaction, prepared)
        val keyColumns = keys.joinToString(",") { transaction.identity(it) }
        val updateColumns = values.keys.filter { it !in keys }.joinToString(",") {
            "${transaction.identity(it)} = EXCLUDED.${transaction.identity(it)}"
        }

        return if (updateColumns.isNotEmpty()) {
            "$insertSql ON CONFLICT ($keyColumns) DO UPDATE SET $updateColumns"
        } else {
            "$insertSql ON CONFLICT ($keyColumns) DO NOTHING"
        }
    }
}

// Extension for upsert functionality (PostgreSQL)
fun <T : Table> T.upsert(
    vararg keys: Column<*>,
    body: T.(UpsertStatement<Number>) -> Unit
) = UpsertStatement<Number>(this, keys = keys).apply {
    body(this)
}