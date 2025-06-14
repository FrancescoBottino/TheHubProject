package com.francescobottino.thehubproject.usecase

import com.francescobottino.thehubproject.client_shared.usecase.CopyToClipboardUseCase
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class DesktopCopyToClipboardUseCase: CopyToClipboardUseCase {
    override suspend operator fun invoke(text: String): Result<Unit> = runCatching {
        val toolkit = Toolkit.getDefaultToolkit()
        val clipboard = toolkit.systemClipboard
        val stringSelection = StringSelection(text)
        clipboard.setContents(stringSelection, null)
    }
}