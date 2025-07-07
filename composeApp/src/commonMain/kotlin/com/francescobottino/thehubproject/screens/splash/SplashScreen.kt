package com.francescobottino.thehubproject.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertCardOverlay
import com.francescobottino.thehubproject.client_features.core.ui.components.AlertState
import com.francescobottino.thehubproject.client_features.core.ui.components.LogoBig
import com.francescobottino.thehubproject.client_features.core.ui.components.VerticalCenteredLayout
import com.francescobottino.thehubproject.client_features.core.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class SplashScreen(private val pendingNavigation: Screen?): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SplashScreenModel(navigator, pendingNavigation) }

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
        VerticalCenteredLayout(
            modifier = Modifier.fillMaxSize(),
            below = {
                if(!state.isError) {
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

        val genericError = remember {
            AlertState(
                title = "Error",
                message = "There was an error while trying to fetch user data.",
                primaryAction = AlertState.Action(
                    label = "Try Again",
                    onClick = { onEvent(SplashScreenEvent.TryAgain) },
                )
            )
        }

        AlertCardOverlay(
            if(state.isError) {
                genericError
            } else {
                null
            }
        )
    }
}

private class SplashScreenStatePreview: PreviewParameterProvider<SplashScreenState> {
    override val values = sequenceOf(
        SplashScreenState(isError = false),
        SplashScreenState(isError = true),
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