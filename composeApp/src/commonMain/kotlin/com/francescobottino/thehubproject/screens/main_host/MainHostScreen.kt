package com.francescobottino.thehubproject.screens.main_host

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import com.francescobottino.thehubproject.client_shared.model.User
import com.francescobottino.thehubproject.client_shared.repo.AuthRepository
import com.francescobottino.thehubproject.client_shared.repo.UserRepository
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import com.francescobottino.thehubproject.navigation.DeepLinkHandler
import com.francescobottino.thehubproject.screens.game_selection.GameSelectionScreen
import com.francescobottino.thehubproject.screens.login.LoginScreen
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

class MainHostScreen(private val pendingNavigation: Screen?): Screen {
    @Composable
    override fun Content() {
        val parentNavigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val deepLinkHandler = koinInject<DeepLinkHandler>()
        val authRepo = koinInject<AuthRepository>()
        val userRepository = koinInject<UserRepository>()
        val user by userRepository.getCurrentUserFlow().collectAsState()

        Navigator(GameSelectionScreen) { navigator ->
            LaunchedEffect(Unit) {
                pendingNavigation?.let {
                    Napier.d { "Navigating to pending navigation screen $pendingNavigation" }
                    navigator.push(it)
                }
                deepLinkHandler.handleDeepLinks(this, navigator)
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopBar(
                        user = user,
                        canBack = navigator.canPop,
                        onBackClicked = { navigator.pop() },
                        onUserClicked = {
                            scope.launch {
                                if (authRepo.isLoggedIn()) { authRepo.logout() }
                                parentNavigator.replace(LoginScreen(null)) //todo on logout this loses current screen. is it desired behavior?
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
    user: User?,
    canBack: Boolean,
    onBackClicked: () -> Unit,
    onUserClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 12.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .requiredHeight(height = 48.dp)
                .fillMaxWidth()
                .padding(vertical = 2.dp),
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .requiredSize(40.dp)
            ) {
                this@Row.AnimatedVisibility(
                    visible = canBack,
                ) {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Icon(
                            FeatherIcons.ArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            }

            //TODO menu to show user and other settings.
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier.fillMaxHeight().weight(1f),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .requiredWidthIn(min = 80.dp)
                        .padding(4.dp)
                        .shadow(elevation = 2.dp, shape = MaterialTheme.shapes.medium)
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                        .clickable(onClick = onUserClicked)
                        .padding(horizontal = 8.dp),
                ) {
                    Text(
                        text = user?.username ?: "Login",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TopBarPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopBar(
                        user = null,
                        canBack = true,
                        {},
                        {},
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(it)) { }
            }
        }
    }
}