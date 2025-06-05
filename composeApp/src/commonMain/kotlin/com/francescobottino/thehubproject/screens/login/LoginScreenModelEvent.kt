package com.francescobottino.thehubproject.screens.login

sealed interface LoginScreenModelEvent {
    data object OnLoggedIn: LoginScreenModelEvent
    data class ErrorPopup(val message: String): LoginScreenModelEvent
}