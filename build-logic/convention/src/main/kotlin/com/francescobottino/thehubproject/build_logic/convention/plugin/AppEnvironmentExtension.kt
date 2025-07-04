package com.francescobottino.thehubproject.build_logic.convention.plugin

import com.francescobottino.thehubproject.build_logic.convention.AppEnvironment

open class AppEnvironmentExtension(val current: AppEnvironment) {
    val all: List<AppEnvironment> = AppEnvironment.values().toList()
}