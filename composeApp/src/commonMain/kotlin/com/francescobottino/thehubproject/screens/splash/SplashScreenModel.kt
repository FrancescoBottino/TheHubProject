package com.francescobottino.thehubproject.screens.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.model.User
import com.francescobottino.thehubproject.network.UnauthorizedError
import com.francescobottino.thehubproject.network.UserApi
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.login.LoginScreen
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

//TODO refactor

class SplashScreenModel(override val di: DI) : ScreenModel, DIAware {
    private val tokenStorage by di.instance<TokenStorage>()
    private val userApi by di.instance<UserApi>()
    private val userRepo by di.instance<UserRepository>()

    suspend fun getFirstScreen(): Screen {
        val token = tokenStorage.getToken()

        if(token == null) {
            return LoginScreen
        }

        val user = userApi.me()
            .onFailure {
                if(it is UnauthorizedError) {
                    return LoginScreen
                } else {
                    // todo Handle error.
                }
            }
            .getOrThrow()

        userRepo.setCurrentUser(User(user.id, user.username))

        return MainHostScreen
    }
}