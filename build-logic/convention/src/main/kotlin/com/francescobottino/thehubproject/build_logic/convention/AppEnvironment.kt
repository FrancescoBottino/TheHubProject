package com.francescobottino.thehubproject.build_logic.convention

enum class AppEnvironment(
    val label: String,
) {
    DEVELOPMENT("dev"),
    STAGING("staging"),
    PRODUCTION("prod");

    companion object {
        fun from(label: String): AppEnvironment {
            return AppEnvironment.values().first { it.label.contains(label, ignoreCase = true) }
        }
    }
}