package com.francescobottino.thehubproject.screens.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.network.ApiService
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.login.LoginScreen
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class SplashScreenModel(override val di: DI) : ScreenModel, DIAware {
    private val tokenStorage by di.instance<TokenStorage>()
    private val api by di.instance<ApiService>()
    private val userRepo by di.instance<UserRepository>()

    suspend fun getFirstScreen(): Screen {
        val token = tokenStorage.getToken()

        if(token == null) {
            return LoginScreen
        }

        val user = api.getMyProfile().getOrNull()

        if(user?.username == null) {
            //todo
            return LoginScreen
        }

        userRepo.setCurrentUser(user)

        return MainHostScreen
    }
}