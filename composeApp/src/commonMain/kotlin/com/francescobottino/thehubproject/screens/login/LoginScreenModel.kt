package com.francescobottino.thehubproject.screens.login

import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.network.ApiService
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class LoginScreenModel(override val di: DI): StatefulScreenModel<LoginScreenState, LoginScreenEvent, LoginScreenModelEvent>(LoginScreenState()), DIAware {
    private val api by di.instance<ApiService>()

    override fun onEvent(event: LoginScreenEvent) {
        when(event) {
            is LoginScreenEvent.OnUsernameChanged -> _state.update { it.copy(username = event.username) }
            is LoginScreenEvent.OnPasswordChanged -> _state.update { it.copy(password = event.password) }
            is LoginScreenEvent.OnLogIn -> onLogIn()
            is LoginScreenEvent.OnRegister -> onRegister()
        }
    }

    private fun onLogIn() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val username = _state.value.username
            val password = _state.value.password

            val result = api.login(AuthRequest(username, password))

            _state.update { it.copy(isLoading = false) }

            result
                .onSuccess { _screenModelEventsFlow.tryEmit(LoginScreenModelEvent.OnLoggedIn) }
                .onFailure { error -> _state.update { it.copy(errorMessage = error.message) } }
        }
    }

    private fun onRegister() {
        _state.update { it.copy(isLoading = true) }
        screenModelScope.launch {
            val username = _state.value.username
            val password = _state.value.password

            val result = api.register(AuthRequest(username, password))

            _state.update { it.copy(isLoading = false) }

            result
                .onSuccess { _screenModelEventsFlow.tryEmit(LoginScreenModelEvent.OnLoggedIn) }
                .onFailure { error -> _state.update { it.copy(errorMessage = error.message) } }
        }
    }
}