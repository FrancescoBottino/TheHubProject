package com.francescobottino.thehubproject.screens.splash

import arrow.core.Either
import arrow.core.left
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.model.User
import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import com.francescobottino.thehubproject.client_shared.screens.StatefulScreenModel
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.screens.login.LoginScreen
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import com.francescobottino.thehubproject.shared.model.UserResponse
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class SplashScreenModel(
    private val navigator: Navigator,
    private val minWaitTimeMillis: Long,
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
                screenModelScope.launch { handleUserResponse(tryFetchUser()) }
            }
        }
    }

    init {
        screenModelScope.launch {
            val user = async { tryFetchUser() }
            val animation = async { delay(minWaitTimeMillis) }
            animation.await()
            handleUserResponse(user.await())
        }
    }

    private fun handleUserResponse(user: Either<FetchUserError, UserResponse>) {
        user
            .onLeft { error ->
                when(error) {
                    FetchUserError.AUTH_ERROR -> navigator.replace(LoginScreen)
                    FetchUserError.FETCH_ERROR -> _state.update { it.copy(isError = true) }
                }
            }
            .onRight { userProfile ->
                userRepo.setCurrentUser(User(userProfile.id, userProfile.username))
                navigator.replace(MainHostScreen)
            }
    }

    private suspend fun tryFetchUser(): Either<FetchUserError, UserResponse> {
        val isLoggedIn = runCatching { authRepo.isLoggedIn() }.getOrElse { false }
        if(!isLoggedIn) {
            return FetchUserError.AUTH_ERROR.left()
        }

        return Either.catch { userApi.me() }
            .fold(
                ifLeft = { FetchUserError.FETCH_ERROR.left() },
                ifRight = { profileResponse ->
                    profileResponse.mapLeft { profileError ->
                        if(profileError == HttpStatusCode.Unauthorized) {
                            FetchUserError.AUTH_ERROR
                        } else {
                            FetchUserError.FETCH_ERROR
                        }
                    }
                }
            )
    }

    enum class FetchUserError {
        AUTH_ERROR,
        FETCH_ERROR;
    }
}