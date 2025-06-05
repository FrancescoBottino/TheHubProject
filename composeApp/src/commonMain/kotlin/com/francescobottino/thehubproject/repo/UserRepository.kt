package com.francescobottino.thehubproject.repo

import com.francescobottino.thehubproject.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun getCurrentUserFlow(): StateFlow<User?>
    fun getCurrentUser(): User?
    fun setCurrentUser(userProfile: User?)
}