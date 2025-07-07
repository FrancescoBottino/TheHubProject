package com.francescobottino.thehubproject.client_features.core.repo

import com.francescobottino.thehubproject.client_features.core.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun getCurrentUserFlow(): StateFlow<User?>
    fun getCurrentUser(): User?
    fun setCurrentUser(userProfile: User?)
}