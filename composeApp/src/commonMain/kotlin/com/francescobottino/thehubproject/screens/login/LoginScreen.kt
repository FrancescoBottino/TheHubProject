package com.francescobottino.thehubproject.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.ui.components.SimpleErrorCardOverlay
import com.francescobottino.thehubproject.client_shared.ui.components.VerticalCenteredLayout
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import compose.icons.FeatherIcons
import compose.icons.feathericons.Eye
import compose.icons.feathericons.EyeOff
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

object LoginScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { LoginScreenModel(navigator) }
        val state by screenModel.state.collectAsState()

        Scaffold {
            LoginScreenContent(
                state = state,
                onEvent = screenModel::onEvent,
                modifier = Modifier.fillMaxSize(),
            )
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
    ) {
        VerticalCenteredLayout(
            content = {
                LoginTextFields(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .requiredWidthIn(max = 320.dp)
                        .fillMaxWidth(),
                )
            },
            below = {
                Column(
                    modifier = Modifier
                        .requiredWidthIn(max = 320.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Actions(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    AnimatedVisibility(
                        visible = state.isLoading,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        )

        AnimatedVisibility(state.isError) {
            SimpleErrorCardOverlay(
                title = "Error",
                message = "There was an error while trying to communicate with the server.",
                action = "Close" to { onEvent(LoginScreenEvent.OnDialogClosed) },
            )
        }
    }
}

@Composable
private fun LoginTextFields(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        UsernameTextField(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth(),
        )

        PasswordTextField(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun UsernameTextField(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier,
        value = state.username,
        onValueChange = { onEvent(LoginScreenEvent.OnUsernameChanged(it)) },
        label = { Text("Username") },
        maxLines = 1,
        isError = state.usernameError != null,
        supportingText = state.usernameError?.let { { Text(it) } },
        enabled = !state.isLoading,
    )
}

@Composable
private fun PasswordTextField(
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

    OutlinedTextField(
        modifier = modifier,
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
}

@Composable
private fun Actions(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
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
}

private class LoginScreenStatePreview: PreviewParameterProvider<LoginScreenState> {
    override val values = LoginScreenState(
        username = "user",
        usernameError = null,
        password = "password",
        passwordError = null,
        isError = false,
        isLoading = false
    ).let { base ->
        sequenceOf(
            base,
            base.copy(usernameError = "Error", passwordError = "Error"),
            base.copy(isLoading = true),
            base.copy(isError = true),
        )
    }
}

@Preview
@Composable
private fun LoginScreenContentPreview(
    @PreviewParameter(LoginScreenStatePreview::class)
    state: LoginScreenState
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LoginScreenContent(
                state = state,
                onEvent = {},
            )
        }
    }
}