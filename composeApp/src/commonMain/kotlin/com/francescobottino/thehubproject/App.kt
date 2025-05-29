package com.francescobottino.thehubproject

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.francescobottino.thehubproject.di.DIProvider
import com.francescobottino.thehubproject.network.ApiService
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kodein.di.compose.localDI
import org.kodein.di.compose.withDI
import org.kodein.di.instance
import thehubproject.composeapp.generated.resources.Res
import thehubproject.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    withDI(DIProvider.di) {
        ApiTestScreen()
    }
}

@Composable
private fun DebugScreen() {
    val scope = rememberCoroutineScope()
    val di = localDI()
    val api by di.instance<ApiService>()

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        var getResult by remember { mutableStateOf<String?>(null) }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }


            Button(onClick = {
                scope.launch {
                    getResult = api.getGreeting().getOrThrow()
                }
            }) {
                Text("Test get from ${Config.httpUrl}")
            }

            Text(text = getResult ?: "Waiting")
        }
    }
}