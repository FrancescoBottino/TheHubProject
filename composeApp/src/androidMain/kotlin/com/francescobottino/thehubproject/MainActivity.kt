package com.francescobottino.thehubproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity: ComponentActivity(), KoinComponent {
    private val deepLinkHandler by inject<DeepLinkHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            Box(Modifier.safeDrawingPadding()) {
                App()
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.toString()?.let { url ->
            deepLinkHandler.onDeepLinkReceived(url)
        }
    }
}