package com.francescobottino.thehubproject.screens

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class StatefulScreenModel<State, ScreenEvent, ScreenModelEvent>: ScreenModel {
    protected val _screenModelEventsFlow = MutableSharedFlow<ScreenModelEvent>(
        replay = 0,
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val screenModelEventsFlow = _screenModelEventsFlow.asSharedFlow()

    abstract val state: StateFlow<State>
    abstract fun onEvent(event: ScreenEvent)
}