package com.francescobottino.thehubproject.client_shared.usecase

interface CopyToClipboardUseCase {
    suspend operator fun invoke(text: String): Result<Unit>
}