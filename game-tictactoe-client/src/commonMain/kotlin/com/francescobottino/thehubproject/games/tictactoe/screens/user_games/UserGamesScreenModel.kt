package com.francescobottino.thehubproject.games.tictactoe.screens.user_games

import com.francescobottino.thehubproject.screens.StatefulScreenModel
import org.kodein.di.DI
import org.kodein.di.DIAware

class UserGamesScreenModel(override val di: DI): StatefulScreenModel<UserGamesScreenState, UserGamesScreenEvent, UserGamesScreenModelEvent>(UserGamesScreenState()), DIAware {
    override fun onEvent(event: UserGamesScreenEvent) {
        TODO("Not yet implemented")
    }
}