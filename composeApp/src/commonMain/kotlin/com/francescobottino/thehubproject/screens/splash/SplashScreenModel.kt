package com.francescobottino.thehubproject.screens.splash

import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.model.User
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import com.francescobottino.thehubproject.screens.login.LoginScreen
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import io.ktor.http.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance


class SplashScreenModel(override val di: DI): StatefulScreenModel<SplashScreenState, SplashScreenEvent, SplashScreenModelEvent>(SplashScreenState()), DIAware {
    private val tokenStorage by di.instance<TokenStorage>()
    private val userApi by di.instance<UserApi>()
    private val userRepo by di.instance<UserRepository>()

    override fun onEvent(event: SplashScreenEvent) {
        when(event) {
            is SplashScreenEvent.TryAgain -> {
                _state.update { it.copy(error = null) }
                screenModelScope.launch { tryInit() }
            }
            is SplashScreenEvent.OnDialogClosed -> _state.update { it.copy(error = null) }
        }
    }

    suspend fun tryInit() {
        try {
            val token = tokenStorage.getToken()

            if(token == null) {
                _screenModelEventsFlow.emit(SplashScreenModelEvent.Navigate(LoginScreen))
                return
            }

            val user = userApi.me()
                .onLeft { errorCode ->
                    if(errorCode == HttpStatusCode.Unauthorized) {
                        _screenModelEventsFlow.emit(SplashScreenModelEvent.Navigate(LoginScreen))
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
            _screenModelEventsFlow.emit(SplashScreenModelEvent.Navigate(MainHostScreen))
            return
        } catch (e: Exception) {
            e.printStackTrace()
            _state.update { it.copy(error = e.message ?: "Unknown error") }
        }
    }
}