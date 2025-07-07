package com.francescobottino.thehubproject.usecase

import com.francescobottino.thehubproject.client_features.core.usecase.CopyToClipboardUseCase
import platform.UIKit.UIPasteboard

class IosCopyToClipboardUseCase(): CopyToClipboardUseCase {
    override suspend operator fun invoke(text: String): Result<Unit> = runCatching {
        UIPasteboard.generalPasteboard.string = text
    }
}