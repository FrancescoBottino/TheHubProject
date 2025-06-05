package com.francescobottino.thehubproject.screens.login

sealed interface LoginScreenEvent {
    data class OnUsernameChanged(val username: String): LoginScreenEvent
    data class OnPasswordChanged(val password: String): LoginScreenEvent
    data object OnLogIn: LoginScreenEvent
    data object OnRegister: LoginScreenEvent
}