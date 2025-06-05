package com.francescobottino.thehubproject.screens.main_host

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.auth.TokenStorage
import com.francescobottino.thehubproject.repo.UserRepository
import com.francescobottino.thehubproject.screens.game_selection.GameSelectionScreen
import com.francescobottino.thehubproject.screens.login.LoginScreen
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import kotlinx.coroutines.launch
import org.kodein.di.compose.localDI
import org.kodein.di.instance

object MainHostScreen: Screen {
    @Composable
    override fun Content() {
        val parentNavigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val tokenStorage by localDI().instance<TokenStorage>()
        val userRepository by localDI().instance<UserRepository>()
        val user by userRepository.getCurrentUserFlow().collectAsState()

        Navigator(GameSelectionScreen) { navigator ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    Surface(
                        shadowElevation = 12.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .requiredHeight(height = 40.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { navigator.pop() },
                                modifier = Modifier.fillMaxHeight(),
                            ) {
                                Icon(
                                    FeatherIcons.ArrowLeft,
                                    contentDescription = "Back"
                                )
                            }

                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) {
                                //todo
                            }

                            Box(
                                modifier = Modifier.requiredWidthIn(min = 120.dp).fillMaxHeight()
                                    .clickable {
                                        scope.launch {
                                            tokenStorage.clearToken()
                                            parentNavigator.replace(LoginScreen)
                                        }
                                    }
                            ) {
                                Text(
                                    text = user?.username ?: ""
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                ) {
                    CurrentScreen()
                }
            }
        }
    }
}