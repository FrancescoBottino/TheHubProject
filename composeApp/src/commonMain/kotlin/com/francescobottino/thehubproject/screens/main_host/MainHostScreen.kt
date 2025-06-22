package com.francescobottino.thehubproject.screens.main_host

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import com.francescobottino.thehubproject.client_shared.ui.components.LogoSmall
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import com.francescobottino.thehubproject.screens.game_selection.GameSelectionScreen
import com.francescobottino.thehubproject.screens.login.LoginScreen
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.LogIn
import compose.icons.feathericons.User
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

object MainHostScreen: Screen {
    @Composable
    override fun Content() {
        val parentNavigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val authRepo = koinInject<AuthRepository>()
        val userRepository = koinInject<UserRepository>()
        val user by userRepository.getCurrentUserFlow().collectAsState()

        Navigator(GameSelectionScreen) { navigator ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopBar(
                        username = user?.username,
                        canBack = navigator.canPop,
                        onBackClicked = { navigator.pop() },
                        onUserClicked = {
                            scope.launch {
                                if (authRepo.isLoggedIn()) { authRepo.logout() }
                                parentNavigator.replace(LoginScreen)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    SlideTransition(navigator)
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    username: String?,
    canBack: Boolean,
    onBackClicked: () -> Unit,
    onUserClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 16.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E).copy(alpha = 0.95f),
                            Color(0xFF16213E).copy(alpha = 0.95f),
                            Color(0xFF0F3460).copy(alpha = 0.95f)
                        )
                    )
                )
                //.backdrop(BlurRadius.MEDIUM) TODO
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .requiredHeight(height = 64.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Back Button
                Box(
                    modifier = Modifier.requiredSize(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = canBack,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        IconButton(
                            onClick = onBackClicked,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                FeatherIcons.ArrowLeft,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Logo Section
                LogoSmall()

                Spacer(modifier = Modifier.weight(1f))

                // User Profile Section
                UserProfileSection(
                    username = username,
                    onClick = onUserClicked
                )
            }
        }
    }
}

@Composable
private fun UserProfileSection(
    username: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF6C63FF).copy(alpha = 0.8f),
                        Color(0xFF5A52FF).copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (username != null) FeatherIcons.User else FeatherIcons.LogIn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Username/Login Text
            Text(
                text = username ?: "Login",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
private fun MainHostScreenContentPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopBar(
                        username = "Alfred",
                        canBack = true,
                        {},
                        {},
                    )
                }
            ) {

            }
        }
    }
}