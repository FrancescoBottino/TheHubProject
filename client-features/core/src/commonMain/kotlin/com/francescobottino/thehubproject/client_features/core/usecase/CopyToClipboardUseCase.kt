package com.francescobottino.thehubproject.client_features.core.usecase

interface CopyToClipboardUseCase {
    suspend operator fun invoke(text: String): Result<Unit>
}