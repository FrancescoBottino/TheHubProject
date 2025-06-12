package com.francescobottino.thehubproject.usecase

interface CopyToClipboardUseCase {
    suspend operator fun invoke(text: String): Result<Unit>
}