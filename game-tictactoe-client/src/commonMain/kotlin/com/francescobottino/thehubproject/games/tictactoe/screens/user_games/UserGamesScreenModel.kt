package com.francescobottino.thehubproject.games.tictactoe.screens.user_games

import com.francescobottino.thehubproject.screens.StatefulScreenModel
import kotlinx.coroutines.flow.StateFlow
import org.kodein.di.DI
import org.kodein.di.DIAware

class UserGamesScreenModel(override val di: DI): StatefulScreenModel<UserGamesScreenState, UserGamesScreenEvent, UserGamesScreenModelEvent>(), DIAware {
    override val state: StateFlow<UserGamesScreenState>
        get() = TODO("Not yet implemented")

    override fun onEvent(event: UserGamesScreenEvent) {
        TODO("Not yet implemented")
    }
}