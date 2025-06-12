package com.francescobottino.thehubproject.usecase

import platform.UIKit.UIPasteboard

class IosCopyToClipboardUseCase(): CopyToClipboardUseCase {
    override suspend operator fun invoke(text: String): Result<Unit> = runCatching {
        UIPasteboard.generalPasteboard.string = text
    }
}