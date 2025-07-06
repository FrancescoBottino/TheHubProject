package com.francescobottino.thehubproject.screens.splash

import arrow.core.getOrElse
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.model.User
import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import com.francescobottino.thehubproject.client_shared.screens.StatefulScreenModel
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.screens.login.LoginScreen
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import io.github.aakira.napier.Napier
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SplashScreenModel(
    private val navigator: Navigator,
    private val pendingNavigation: Screen?,
): StatefulScreenModel<SplashScreenState, SplashScreenEvent>(), KoinComponent {
    private val authRepo by inject<AuthRepository>()
    private val userApi by inject<UserApi>()
    private val userRepo by inject<UserRepository>()

    private val _state = MutableStateFlow(SplashScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: SplashScreenEvent) {
        when(event) {
            is SplashScreenEvent.TryAgain -> {
                _state.update { it.copy(isError = false) }
                screenModelScope.launch { tryInit() }
            }
        }
    }

    init {
        Napier.d(tag = "SplashScreenModel") { "On splash screen init" }
        screenModelScope.launch { tryInit() }
    }

    private suspend fun tryInit() {
        Napier.d(tag = "SplashScreenModel") { "trying app init" }
        val isLoggedIn = runCatching { authRepo.isLoggedIn() }.getOrElse { false }
        if(!isLoggedIn) {
            Napier.d(tag = "SplashScreenModel") { "not logged in, showing login screen" }
            navigator.replace(LoginScreen(pendingNavigation = pendingNavigation))
            return
        }

        val userProfile = runCatching { userApi.me() }
            .getOrElse { exception ->
                Napier.d(tag = "SplashScreenModel") { "error in fetching user profile: $exception" }
                _state.update {
                    it.copy(isError = true)
                }
                return
            }
            .getOrElse { profileError ->
                Napier.d(tag = "SplashScreenModel") { "user profile response is an error: $profileError" }
                if(profileError == HttpStatusCode.Unauthorized) {
                    Napier.d(tag = "SplashScreenModel") { "showing login screen" }
                    navigator.replace(LoginScreen(pendingNavigation = pendingNavigation))
                } else {
                    _state.update { it.copy(isError = true) }
                }
                return
            }

        Napier.d(tag = "SplashScreenModel") { "Syncing user info ans starting app" }
        userRepo.setCurrentUser(User(userProfile.id, userProfile.username))
        navigator.replace(MainHostScreen(pendingNavigation = pendingNavigation))
        return
    }
}