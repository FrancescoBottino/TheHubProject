package com.francescobottino.thehubproject.client_shared.repo

import com.francescobottino.thehubproject.client_shared.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryUserRepository: UserRepository {
    private val currentUserStateFloat = MutableStateFlow<User?>(null)
    override fun getCurrentUserFlow(): StateFlow<User?> {
        return currentUserStateFloat.asStateFlow()
    }

    override fun getCurrentUser(): User? {
        return currentUserStateFloat.value
    }

    override fun setCurrentUser(userProfile: User?) {
        currentUserStateFloat.value = userProfile
    }
}