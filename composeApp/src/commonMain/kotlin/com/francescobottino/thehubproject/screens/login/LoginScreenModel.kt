package com.francescobottino.thehubproject.screens.login

import arrow.core.Either
import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.api.auth.AuthRequest
import com.francescobottino.thehubproject.api.auth.AuthResponse
import com.francescobottino.thehubproject.auth.AuthApi
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.model.User
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class LoginScreenModel(override val di: DI): StatefulScreenModel<LoginScreenState, LoginScreenEvent, LoginScreenModelEvent>(LoginScreenState()), DIAware {
    private val authApi by di.instance<AuthApi>()
    private val tokenStorage by di.instance<TokenStorage>()
    private val userRepository by di.instance<UserRepository>()

    override fun onEvent(event: LoginScreenEvent) {
        when(event) {
            is LoginScreenEvent.OnUsernameChanged -> _state.update { it.copy(username = event.username) }
            is LoginScreenEvent.OnPasswordChanged -> _state.update { it.copy(password = event.password) }
            is LoginScreenEvent.OnLogIn -> performAuth(endpoint = authApi::login)
            is LoginScreenEvent.OnRegister -> performAuth(endpoint = authApi::register)
        }
    }

    private fun performAuth(endpoint: suspend (AuthRequest) -> Either<AuthResponse.Error, AuthResponse.Success>) {
        _state.update { it.copy(isLoading = true, usernameError = null, passwordError = null, errorMessage = null) }
        screenModelScope.launch {
            val username = _state.value.username
            val password = _state.value.password

            runCatching {
                endpoint(AuthRequest(username, password))
                    .onLeft { error ->
                        when(error) {
                            is AuthResponse.UserAlreadyExists,
                            is AuthResponse.UserNotFound -> _state.update { it.copy(usernameError = error.message) }
                            is AuthResponse.IncorrectPassword -> _state.update { it.copy(passwordError = error.message) }
                            else -> _state.update { it.copy(errorMessage = error.message) }
                        }
                    }
                    .onRight { response ->
                        tokenStorage.saveToken(response.token)
                        userRepository.setCurrentUser(User(id = response.userId, username = response.username))
                        _screenModelEventsFlow.tryEmit(LoginScreenModelEvent.OnLoggedIn)
                    }
            }.onFailure { error ->
                _screenModelEventsFlow.tryEmit(LoginScreenModelEvent.ErrorPopup(error.message ?: "Unknown error"))
            }
        }.invokeOnCompletion {
            _state.update { it.copy(isLoading = false) }
        }
    }
}