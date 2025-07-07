package com.francescobottino.thehubproject.shared_features.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginationParams(
    val page: Int = 1,
    val limit: Int = 20
) {
    val offset: Int get() = (page - 1) * limit
    
    init {
        require(page > 0) { "Page must be positive" }
        require(limit > 0 && limit <= 100) { "Limit must be between 1 and 100" }
    }
}