package com.francescobottino.thehubproject.screens

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class StatefulScreenModel<State, ScreenEvent, ScreenModelEvent>(
    initialState: State,
): ScreenModel {
    protected val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    protected val _screenModelEventsFlow = MutableSharedFlow<ScreenModelEvent>(
        replay = 0,
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val screenModelEventsFlow = _screenModelEventsFlow.asSharedFlow()

    abstract fun onEvent(event: ScreenEvent)
}