package com.francescobottino.thehubproject.screens.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.ui.components.LogoBig
import com.francescobottino.thehubproject.client_shared.ui.components.SimpleErrorCardOverlay
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

object SplashScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SplashScreenModel(navigator) }

        val state by screenModel.state.collectAsState()

        Scaffold {
            SplashScreenContent(
                state = state,
                onEvent = screenModel::onEvent,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SplashScreenContent(
    state: SplashScreenState,
    onEvent: (SplashScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        CenterMainContent(
            modifier = Modifier.fillMaxSize(),
            below = {
                if(!state.error) {
                    Box(
                        modifier = Modifier.requiredSize(64.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        ) {
            LogoBig()
        }

        if(state.error) {
            SimpleErrorCardOverlay(
                title = "Error",
                message = "There was an error while trying to fetch user data.",
                action = "Try Again" to { onEvent(SplashScreenEvent.TryAgain) },
            )
        }
    }
}

@Composable
private fun CenterMainContent(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    above: @Composable BoxScope.() -> Unit = {},
    below: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter,
            content = above,
        )

        Box(
            contentAlignment = Alignment.Center,
            content = content
        )

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
            content = below
        )
    }
}

private class SplashScreenStatePreview: PreviewParameterProvider<SplashScreenState> {
    override val values = sequenceOf(
        SplashScreenState(error = false),
        SplashScreenState(error = true),
    )
}

@Preview
@Composable
private fun SplashScreenContentPreviewWithError(
    @PreviewParameter(SplashScreenStatePreview::class)
    state: SplashScreenState
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            SplashScreenContent(state, {})
        }
    }
}