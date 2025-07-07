package com.francescobottino.thehubproject.client_shared.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class AlertState(
    val title: String,
    val message: String? = null,
    val dismissable: Boolean = false,
    val primaryAction: Action? = null,
    val secondaryAction: Action? = null,
) {
    data class Action(
        val label: String,
        val onClick: () -> Unit,
    )
}

@Composable
fun AlertCardOverlay(
    state: AlertState?,
    onDismissRequest: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var lastState by remember { mutableStateOf(state) }

    LaunchedEffect(state) {
        if(state != null) {
            lastState = state
        }
    }

    DialogCardOverlay(
        visible = state != null,
        modifier = modifier,
        onDismissRequest = {
            if(state?.dismissable == true) {
                onDismissRequest()
            }
        }
    ) {
        lastState?.let { lastState ->
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.requiredWidthIn(min = 160.dp),
            ) {
                Text(
                    text = lastState.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp),
                )

                lastState.message?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 32.dp),
                    )
                }

                if(lastState.primaryAction != null || lastState.secondaryAction != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(End)
                            .padding(top = 6.dp)
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 8.dp),
                    ) {
                        if(lastState.secondaryAction != null) {
                            TextButton(
                                onClick = lastState.secondaryAction.onClick,
                            ) {
                                Text(lastState.secondaryAction.label)
                            }
                        }
                        if(lastState.primaryAction != null) {
                            Button(
                                onClick = lastState.primaryAction.onClick,
                            ) {
                                Text(lastState.primaryAction.label)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun AlertCardOverlayPreview() {
    val error = remember {
        AlertState(
            "Generic Error",
            "An error occurred while processing your request. Please try again later.",
            false,
            AlertState.Action("Retry", {}),
        )
    }

    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AlertCardOverlay(error)
        }
    }
}