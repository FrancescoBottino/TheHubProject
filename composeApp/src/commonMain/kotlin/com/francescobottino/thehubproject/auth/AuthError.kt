package com.francescobottino.thehubproject.auth

import com.francescobottino.thehubproject.api.auth.AuthResponse

class AuthError(val error: AuthResponse.Error): Exception(error.message)