package com.francescobottino.thehubproject.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("ComposableNaming")
@Composable
fun <State, ScreenEvent, ScreenModelEvent> StatefulScreenModel<State, ScreenEvent, ScreenModelEvent>.manageEvents(
    onScreenModelEvent: suspend (ScreenModelEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        screenModelEventsFlow
            .onEach(onScreenModelEvent)
            .catch {  }
            .launchIn(this)
    }
}