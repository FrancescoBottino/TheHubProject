package com.francescobottino.thehubproject.repo

import com.francescobottino.thehubproject.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryUserRepository: UserRepository {
    private val currentUserStateFloat = MutableStateFlow<UserProfile?>(null)
    override fun getCurrentUserFlow(): StateFlow<UserProfile?> {
        return currentUserStateFloat.asStateFlow()
    }

    override fun getCurrentUser(): UserProfile? {
        return currentUserStateFloat.value
    }

    override fun setCurrentUser(userProfile: UserProfile?) {
        currentUserStateFloat.value = userProfile
    }
}