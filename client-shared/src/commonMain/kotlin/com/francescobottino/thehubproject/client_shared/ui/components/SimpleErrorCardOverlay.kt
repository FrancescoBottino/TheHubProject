package com.francescobottino.thehubproject.client_shared.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class SimpleError(
    val title: String,
    val message: String? = null,
    val action: Pair<String, () -> Unit>? = null,
)

@Composable
fun SimpleErrorCardOverlay(
    error: SimpleError?,
    onDismissRequest: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var lastError by remember { mutableStateOf(error) }

    LaunchedEffect(error) {
        if(error != null) {
            lastError = error
        }
    }

    DialogCardOverlay(
        visible = error != null,
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        lastError?.let { lastError ->
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.requiredWidthIn(min = 160.dp),
            ) {
                Text(
                    text = lastError.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp),
                )

                lastError.message?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 32.dp),
                    )
                }

                lastError.action?.let { (label, onClick) ->
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
}

@Preview
@Composable
private fun SimpleErrorCardOverlayPreview() {
    var error by remember { mutableStateOf<SimpleError?>(null) }

    val errorCache = remember {
        SimpleError(
            "Generic Error",
            "An error occurred while processing your request. Please try again later.",
            "Retry" to { error = null },
        )
    }

    AppTheme {
        Surface(modifier = Modifier.fillMaxSize().clickable { error = errorCache }) {
            SimpleErrorCardOverlay(error)
        }
    }
}