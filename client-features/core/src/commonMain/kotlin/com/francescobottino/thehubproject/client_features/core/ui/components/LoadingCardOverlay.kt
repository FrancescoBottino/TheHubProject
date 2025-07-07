package com.francescobottino.thehubproject.client_features.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingCardOverlay(
    visible: Boolean = true,
    text: String = "Loading...",
    modifier: Modifier = Modifier,
) {
    DialogCardOverlay(
        visible = visible,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
            )

            Text(text = text)
        }
    }
}

@Preview
@Composable
private fun LoadingCardOverlayPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoadingCardOverlay()
        }
    }
}