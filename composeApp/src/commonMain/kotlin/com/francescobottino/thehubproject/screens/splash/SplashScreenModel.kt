package com.francescobottino.thehubproject.screens.splash

import arrow.core.getOrElse
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.model.User
import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import com.francescobottino.thehubproject.client_shared.screens.StatefulScreenModel
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.screens.login.LoginScreen
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SplashScreenModel(
    private val navigator: Navigator,
): StatefulScreenModel<SplashScreenState, SplashScreenEvent>(), KoinComponent {
    private val authRepo by inject<AuthRepository>()
    private val userApi by inject<UserApi>()
    private val userRepo by inject<UserRepository>()

    private val _state = MutableStateFlow(SplashScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: SplashScreenEvent) {
        when(event) {
            is SplashScreenEvent.TryAgain -> {
                _state.update { it.copy(error = false) }
                screenModelScope.launch { tryInit() }
            }
        }
    }

    init {
        screenModelScope.launch { tryInit() }
    }

    private suspend fun tryInit() {
        val isLoggedIn = runCatching { authRepo.isLoggedIn() }.getOrElse { false }
        if(!isLoggedIn) {
            navigator.replace(LoginScreen)
            return
        }

        val userProfile = runCatching { userApi.me() }
            .getOrElse { exception ->
                _state.update {
                    it.copy(error = true)
                }
                return
            }
            .getOrElse { profileError ->
                if(profileError == HttpStatusCode.Unauthorized) {
                    navigator.replace(LoginScreen)
                } else {
                    _state.update {
                        it.copy(error = true)
                    }
                }
                return
            }

        userRepo.setCurrentUser(User(userProfile.id, userProfile.username))
        navigator.replace(MainHostScreen)
        return
    }
}