package com.francescobottino.thehubproject.screens.login

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)