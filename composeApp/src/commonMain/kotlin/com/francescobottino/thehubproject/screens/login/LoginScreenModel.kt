package com.francescobottino.thehubproject.screens.login

import arrow.core.Either
import cafe.adriel.voyager.core.model.screenModelScope
import com.francescobottino.thehubproject.auth.AuthApi
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.model.AuthResponseError
import com.francescobottino.thehubproject.model.AuthResponseSuccess
import com.francescobottino.thehubproject.model.User
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.StatefulScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class LoginScreenModel(override val di: DI): StatefulScreenModel<LoginScreenState, LoginScreenEvent, LoginScreenModelEvent>(), DIAware {
    private val authApi by di.instance<AuthApi>()
    private val tokenStorage by di.instance<TokenStorage>()
    private val userRepository by di.instance<UserRepository>()

    private val _state = MutableStateFlow(LoginScreenState())
    override val state = _state.asStateFlow()

    override fun onEvent(event: LoginScreenEvent) {
        when(event) {
            is LoginScreenEvent.OnUsernameChanged -> _state.update { it.copy(username = event.username) }
            is LoginScreenEvent.OnPasswordChanged -> _state.update { it.copy(password = event.password) }
            is LoginScreenEvent.OnLogIn -> performAuth(endpoint = authApi::login)
            is LoginScreenEvent.OnRegister -> performAuth(endpoint = authApi::register)
            is LoginScreenEvent.OnDialogClosed -> _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue.drop(1)) }
        }
    }

    private fun performAuth(endpoint: suspend (AuthRequest) -> Either<AuthResponseError, AuthResponseSuccess>) {
        _state.update { it.copy(isLoading = true, usernameError = null, passwordError = null, errorMessage = null) }
        screenModelScope.launch {
            val username = _state.value.username
            val password = _state.value.password

            runCatching { endpoint(AuthRequest(username, password)) }
                .onFailure { error ->
                    _state.update { it.copy(dialogMessagesQueue = it.dialogMessagesQueue + (error.message ?: "Unknown error")) }
                }
                .onSuccess { response ->
                    response.onLeft { error ->
                        when(error) {
                            AuthResponseError.USER_ALREADY_EXISTS -> _state.update { it.copy(usernameError = "Username already in use.") }
                            AuthResponseError.USER_NOT_FOUND -> _state.update { it.copy(usernameError = "Username not found.") }
                            AuthResponseError.INCORRECT_PASSWORD -> _state.update { it.copy(passwordError = "Password is incorrect") }
                        }
                    }.onRight { successResponse ->
                        tokenStorage.saveToken(successResponse.token)
                        userRepository.setCurrentUser(User(id = successResponse.userId, username = successResponse.username))
                        _screenModelEventsFlow.tryEmit(LoginScreenModelEvent.OnLoggedIn)
                    }
                }
        }.invokeOnCompletion {
            _state.update { it.copy(isLoading = false) }
        }
    }
}