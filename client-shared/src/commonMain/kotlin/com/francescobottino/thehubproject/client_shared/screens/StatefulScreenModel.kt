package com.francescobottino.thehubproject.client_shared.screens

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.StateFlow

abstract class StatefulScreenModel<State, ScreenEvent>: ScreenModel {
    abstract val state: StateFlow<State>
    abstract fun onEvent(event: ScreenEvent)
}