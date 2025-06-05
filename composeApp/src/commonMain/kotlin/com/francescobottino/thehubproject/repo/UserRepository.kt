package com.francescobottino.thehubproject.repo

import com.francescobottino.thehubproject.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    fun getCurrentUserFlow(): StateFlow<UserProfile?>
    fun getCurrentUser(): UserProfile?
    fun setCurrentUser(userProfile: UserProfile?)
}