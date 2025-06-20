package com.francescobottino.thehubproject.client_shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SimpleErrorCardOverlay(
    title: String? = null,
    message: String? = null,
    action: Pair<String, () -> Unit>? = null,
    modifier: Modifier = Modifier,
) {
    DialogCardOverlay(modifier = modifier) {
        Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.requiredWidthIn(min = 160.dp),
        ) {
            Text(
                text = title ?: "Error",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 16.dp),
            )

            message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 32.dp),
                )
            }

            action?.let { (label, onClick) ->
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .align(End)
                        .padding(top = 6.dp)
                        .padding(bottom = 8.dp)
                        .padding(horizontal = 8.dp),
                ) {
                    Text(text = label)
                }
            }
        }
    }
}

@Preview
@Composable
private fun SimpleErrorCardOverlayPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SimpleErrorCardOverlay(
                title = "Generic Error very long line lorem ipsium",
                message = "An error occurred while processing your request. Please try again later.",
                action = "Retry" to {},
            )
        }
    }
}