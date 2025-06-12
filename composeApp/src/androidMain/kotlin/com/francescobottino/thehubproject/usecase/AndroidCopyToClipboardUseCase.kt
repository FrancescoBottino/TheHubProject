package com.francescobottino.thehubproject.usecase

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class AndroidCopyToClipboardUseCase(private val context: Context): CopyToClipboardUseCase {
    override suspend fun invoke(text: String): Result<Unit> = runCatching {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }
}