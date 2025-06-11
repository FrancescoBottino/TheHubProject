package com.francescobottino.thehubproject.screens.splash

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.model.User
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.repo.AuthRepository
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import com.francescobottino.thehubproject.screens.login.LoginScreen
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance


class SplashScreenModel(
    override val di: DI,
    private val navigator: Navigator,
): StatefulScreenModel<SplashScreenState, SplashScreenEvent>(), DIAware {
    private val authRepo by di.instance<AuthRepository>()
    private val userApi by di.instance<UserApi>()
    private val userRepo by di.instance<UserRepository>()

    private val _state = MutableStateFlow(SplashScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: SplashScreenEvent) {
        when(event) {
            is SplashScreenEvent.TryAgain -> {
                _state.update { it.copy(error = null) }
                screenModelScope.launch { tryInit() }
            }
        }
    }

    init {
        screenModelScope.launch { tryInit() }
    }

    private suspend fun tryInit() {
        try {
            if(!authRepo.isLoggedIn()) {
                navigator.replace(LoginScreen)
                return
            }

            val user = userApi.me()
                .onLeft { errorCode ->
                    if(errorCode == HttpStatusCode.Unauthorized) {
                        navigator.replace(LoginScreen)
                        return
                    } else {
                        _state.update { it.copy(error = "Error getting user profile") }
                        return
                    }
                }
                .getOrNull()
                ?: run {
                    _state.update { it.copy(error = "Unknown error") }
                    return
                }

            userRepo.setCurrentUser(User(user.id, user.username))
            navigator.replace(MainHostScreen)
            return
        } catch (e: Exception) {
            e.printStackTrace()
            _state.update { it.copy(error = e.message ?: "Unknown error") }
        }
    }
}