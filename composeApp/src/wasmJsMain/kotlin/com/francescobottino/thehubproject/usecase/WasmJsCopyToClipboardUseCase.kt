package com.francescobottino.thehubproject.usecase

import com.francescobottino.thehubproject.client_features.core.usecase.CopyToClipboardUseCase
import kotlinx.browser.document
import kotlinx.coroutines.await
import org.w3c.dom.HTMLTextAreaElement
import kotlin.js.Promise

class WasmJsCopyToClipboardUseCase: CopyToClipboardUseCase {
    override suspend fun invoke(text: String): Result<Unit> = runCatching {
        when {
            // Controlla se l'API Clipboard è disponibile e se siamo in un contesto sicuro
            isClipboardApiAvailable && isSecureContext -> {
                copyWithModernApi(text).await()
            }
            // Fallback: usa il vecchio metodo (funziona solo se chiamato da un evento utente)
            else -> {
                copyWithLegacyMethod(text)
            }
        }
    }

    private fun copyWithLegacyMethod(text: String) {
        val textArea = document.createElement("textarea")
        textArea.setAttribute("style", "position: fixed; top: -9999px; left: -9999px; opacity: 0;")
        textArea.setAttribute("value", text)
        textArea.setAttribute("readonly", "")

        document.body?.appendChild(textArea)

        try {
            val textAreaElement = textArea as HTMLTextAreaElement
            textAreaElement.select()
            textAreaElement.setSelectionRange(0, text.length)

            val successful = document.execCommand("copy")
            if (!successful) {
                throw Exception("execCommand('copy') failed")
            }
        } finally {
            document.body?.removeChild(textArea)
        }
    }
}

private val isClipboardApiAvailable: Boolean = js("typeof navigator !== 'undefined' && navigator.clipboard && typeof navigator.clipboard.writeText === 'function'")

private val isSecureContext: Boolean = js("typeof window !== 'undefined' && (window.location.protocol === 'https:' || window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')")

private fun copyWithModernApi(text: String): Promise<JsAny?> { js("navigator.clipboard.writeText(text)") }