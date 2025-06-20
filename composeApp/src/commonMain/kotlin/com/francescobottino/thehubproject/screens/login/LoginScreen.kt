package com.francescobottino.thehubproject.screens.login

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.Eye
import compose.icons.feathericons.EyeOff
import org.jetbrains.compose.ui.tooling.preview.Preview

object LoginScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { LoginScreenModel(navigator) }
        val state by screenModel.state.collectAsState()

        LoginScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun LoginScreenContent(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val passwordTrailingIcon = if(passwordVisible) {
        FeatherIcons.EyeOff
    } else {
        FeatherIcons.Eye
    }

    val passwordVisualTransformation = if(passwordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.username,
                onValueChange = { onEvent(LoginScreenEvent.OnUsernameChanged(it)) },
                label = { Text("Username") },
                maxLines = 1,
                isError = state.usernameError != null,
                supportingText = state.usernameError?.let { { Text(it) } },
                enabled = !state.isLoading,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { onEvent(LoginScreenEvent.OnPasswordChanged(it)) },
                label = { Text("Password") },
                isError = state.passwordError != null,
                maxLines = 1,
                supportingText = state.passwordError?.let { { Text(it) } },
                visualTransformation = passwordVisualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                    ) {
                        Icon(
                            imageVector = passwordTrailingIcon,
                            contentDescription = null,
                        )
                    }
                },
                enabled = !state.isLoading,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                Button(
                    enabled = !state.isLoading,
                    onClick = { onEvent(LoginScreenEvent.OnLogIn) },
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

            AnimatedVisibility(
                visible = state.isLoading,
            ) {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if(state.dialogMessagesQueue.isNotEmpty()) {
        val dialogMessage = state.dialogMessagesQueue.first()

        Dialog(
            onDismissRequest = { onEvent(LoginScreenEvent.OnDialogClosed) }
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
                    Button(onClick = { onEvent(LoginScreenEvent.OnDialogClosed) }) {
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenContentPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LoginScreenContent(
                state = LoginScreenState(isLoading = false),
                onEvent = {},
            )
        }
    }
}