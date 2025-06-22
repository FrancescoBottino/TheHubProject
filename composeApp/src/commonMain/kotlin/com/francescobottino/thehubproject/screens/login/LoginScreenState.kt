package com.francescobottino.thehubproject.screens.login

data class LoginScreenState(
    val username: String = "",
    val usernameError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isError: Boolean = false,
    val isLoading: Boolean = false,
)