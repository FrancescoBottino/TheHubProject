package com.francescobottino.thehubproject

import androidx.compose.runtime.Composable
import com.francescobottino.thehubproject.di.DIProvider
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.compose.withDI

@Composable
@Preview
fun App() {
    withDI(DIProvider.di) {
        ApiTestScreen()
    }
}