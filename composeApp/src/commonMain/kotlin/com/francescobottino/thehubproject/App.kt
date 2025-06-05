package com.francescobottino.thehubproject

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.screens.splash.SplashScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.compose.withDI

@Composable
@Preview
fun App() {
    withDI(DIProvider.di) {
        MaterialTheme {
            Navigator(SplashScreen)
        }
    }
}