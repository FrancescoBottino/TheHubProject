package com.francescobottino.thehubproject.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.screens.manageEvents
import org.kodein.di.compose.localDI

object SplashScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val screenModel = rememberScreenModel { SplashScreenModel(di) }
        val navigator = LocalNavigator.currentOrThrow

        val state by screenModel.state.collectAsState()

        SplashScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )

        screenModel.manageEvents { screenModelEvent ->
            when(screenModelEvent) {
                is SplashScreenModelEvent.Navigate -> { navigator.replace(screenModelEvent.screen) }
            }
        }

        LaunchedEffect(Unit) {
            screenModel.tryInit()
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = "The Hub Project",
                style = MaterialTheme.typography.titleMedium,
            )

            Box(
                modifier = Modifier.requiredSize(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        if(state.error != null) {
            Dialog(
                onDismissRequest = {}
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                        modifier = Modifier
                            .shadow(elevation = 12.dp)
                            .background(color = Color.White)
                            .padding(16.dp),
                    ) {
                        Text(state.error)
                        Button(onClick = { onEvent(SplashScreenEvent.TryAgain) }) {
                            Text("Try again")
                        }
                    }
                }
            }
        }
    }
}