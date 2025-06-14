package com.francescobottino.thehubproject.client_shared.repo

import com.francescobottino.thehubproject.client_shared.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun getCurrentUserFlow(): StateFlow<User?>
    fun getCurrentUser(): User?
    fun setCurrentUser(userProfile: User?)
}