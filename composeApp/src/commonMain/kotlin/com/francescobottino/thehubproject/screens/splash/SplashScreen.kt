package com.francescobottino.thehubproject.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.kodein.di.compose.localDI

object SplashScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val screenModel = rememberScreenModel { SplashScreenModel(di) }

        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = "The Hub Project",
                style = MaterialTheme.typography.titleMedium,
            )

            CircularProgressIndicator(
                modifier = Modifier.requiredSize(64.dp)
            )
        }

        LaunchedEffect(Unit) {
            navigator.replace(screenModel.getFirstScreen())
        }
    }
}