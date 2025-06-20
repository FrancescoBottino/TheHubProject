package com.francescobottino.thehubproject

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import com.francescobottino.thehubproject.screens.splash.SplashScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme {
        Navigator(SplashScreen)
    }
}