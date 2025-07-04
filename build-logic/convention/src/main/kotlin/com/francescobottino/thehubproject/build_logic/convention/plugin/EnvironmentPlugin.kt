package com.francescobottino.thehubproject.build_logic.convention.plugin

import com.francescobottino.thehubproject.build_logic.convention.AppEnvironment
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

@Suppress("unused")
class EnvironmentPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        val environment: String by target
        val currentEnv = AppEnvironment.from(environment)
        target.extensions.create("appEnvironment", AppEnvironmentExtension::class.java, currentEnv)
    }
}