package com.francescobottino.thehubproject.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.screens.main_host.MainHostScreen
import com.francescobottino.thehubproject.screens.manageEvents
import org.kodein.di.compose.localDI

object LoginScreen: Screen {
    @Composable
    override fun Content() {
        val di = localDI()
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { LoginScreenModel(di) }
        val state by screenModel.state.collectAsState()
        var dialogMessagesQueue by remember { mutableStateOf<List<String>>(emptyList()) }

        LoginScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )

        if(state.isLoading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier.requiredSize(64.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        if(dialogMessagesQueue.isNotEmpty()) {
            val dialogMessage = dialogMessagesQueue.first()

            Dialog(
                onDismissRequest = { dialogMessagesQueue = dialogMessagesQueue.drop(1) }
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
                        Text(dialogMessage)
                        Button(onClick = { dialogMessagesQueue = dialogMessagesQueue.drop(1) }) {
                            Text("OK")
                        }
                    }
                }
            }
        }

        screenModel.manageEvents { event ->
            when(event) {
                is LoginScreenModelEvent.OnLoggedIn -> {
                    navigator.replace(MainHostScreen)
                }
                is LoginScreenModelEvent.ErrorPopup -> {
                    dialogMessagesQueue += event.message
                }
            }
        }
    }
}

@Composable
private fun LoginScreenContent(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.username,
                onValueChange = { onEvent(LoginScreenEvent.OnUsernameChanged(it)) },
                label = { Text("Username") },
                isError = state.usernameError != null,
                supportingText = state.usernameError?.let { { Text(it) } },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { onEvent(LoginScreenEvent.OnPasswordChanged(it)) },
                label = { Text("Password") },
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                Button(
                    enabled = !state.isLoading,
                    onClick = { onEvent(LoginScreenEvent.OnLogIn) }
                ) {
                    Text("Log In")
                }
                Button(
                    enabled = !state.isLoading,
                    onClick = { onEvent(LoginScreenEvent.OnRegister) }
                ) {
                    Text("Register")
                }
            }
            Text(
                text = state.errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}