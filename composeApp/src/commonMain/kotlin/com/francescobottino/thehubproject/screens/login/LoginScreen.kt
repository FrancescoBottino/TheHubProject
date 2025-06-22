package com.francescobottino.thehubproject.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
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
import compose.icons.feathericons.*
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
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460),
                        Color(0xFF0A1A3A)
                    ),
                    radius = 1200f
                )
            )
    ) {
        VerticalCenteredLayout(
            content = {
                LoginCard(
                    state = state,
                    onEvent = onEvent,
                    modifier = Modifier
                        .requiredWidthIn(max = 400.dp)
                        .fillMaxWidth()
                )
            },
            below = {
                AnimatedVisibility(
                    visible = state.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LoadingIndicator()
                }
            }
        )

        if(state.isError) {
            SimpleErrorCardOverlay(
                title = "Error",
                message = "There was an error while trying to communicate with the server.",
                action = "Close" to { onEvent(LoginScreenEvent.OnDialogClosed) },
            )
        }
    }
}

@Composable
private fun LoginCard(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            LoginHeader()

            // Text Fields
            LoginTextFields(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.fillMaxWidth()
            )

            // Actions
            Actions(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6C63FF),
                            Color(0xFF5A52FF)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = FeatherIcons.LogIn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )

        Text(
            text = "Sign in to your account",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
        label = {
            Text(
                "Username",
                color = Color.White.copy(alpha = 0.7f)
            )
        },
        placeholder = {
            Text(
                "Enter your username",
                color = Color.White.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = FeatherIcons.User,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f)
            )
        },
        maxLines = 1,
        isError = state.usernameError != null,
        supportingText = state.usernameError?.let {
            {
                Text(
                    it,
                    color = Color(0xFFFF6B6B)
                )
            }
        },
        enabled = !state.isLoading,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6C63FF),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            errorBorderColor = Color(0xFFFF6B6B),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF6C63FF)
        ),
        shape = RoundedCornerShape(16.dp)
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
        label = {
            Text(
                "Password",
                color = Color.White.copy(alpha = 0.7f)
            )
        },
        placeholder = {
            Text(
                "Enter your password",
                color = Color.White.copy(alpha = 0.5f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = FeatherIcons.Lock,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f)
            )
        },
        isError = state.passwordError != null,
        maxLines = 1,
        supportingText = state.passwordError?.let {
            {
                Text(
                    it,
                    color = Color(0xFFFF6B6B)
                )
            }
        },
        visualTransformation = passwordVisualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
            ) {
                Icon(
                    imageVector = passwordTrailingIcon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        enabled = !state.isLoading,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6C63FF),
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            errorBorderColor = Color(0xFFFF6B6B),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF6C63FF)
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun Actions(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Login Button
        LoginButton(
            text = "Sign In",
            onClick = { onEvent(LoginScreenEvent.OnLogIn) },
            enabled = !state.isLoading,
            isPrimary = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Register Button
        LoginButton(
            text = "Create Account",
            onClick = { onEvent(LoginScreenEvent.OnRegister) },
            enabled = !state.isLoading,
            isPrimary = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LoginButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    isPrimary: Boolean,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF6C63FF).copy(alpha = 0.5f)
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent
            )
        },
        border = if (!isPrimary) {
            BorderStroke(2.dp, Color.White.copy(alpha = 0.3f))
        } else null,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                Color.White.copy(alpha = 0.1f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF6C63FF),
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )
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