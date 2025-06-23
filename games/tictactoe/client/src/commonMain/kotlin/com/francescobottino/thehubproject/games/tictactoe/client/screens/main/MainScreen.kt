package com.francescobottino.thehubproject.games.tictactoe.client.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.ui.components.LoadingCardOverlay
import com.francescobottino.thehubproject.client_shared.ui.components.SimpleErrorCardOverlay
import com.francescobottino.thehubproject.client_shared.ui.theme.GameHubDimensions
import com.francescobottino.thehubproject.client_shared.ui.theme.GameHubShapes
import com.francescobottino.thehubproject.client_shared.ui.theme.GameHubTheme
import com.francescobottino.thehubproject.client_shared.ui.theme.GameHubThemeUtils
import compose.icons.FeatherIcons
import compose.icons.feathericons.ChevronRight
import compose.icons.feathericons.LogIn
import compose.icons.feathericons.Plus
import compose.icons.feathericons.User
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

object MainScreen: Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MainScreenModel(navigator) }
        val state by screenModel.state.collectAsState()

        MainScreenContent(
            state = state,
            onEvent = screenModel::onEvent,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun MainScreenContent(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = GameHubThemeUtils.getBackgroundBrush())
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GameHubDimensions.spacingXXLarge, Alignment.Top),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GameHubDimensions.paddingLarge)
        ) {
            Spacer(Modifier.height(GameHubDimensions.spacingHuge))

            // Title Section
            GameTitle()

            Spacer(Modifier.height(GameHubDimensions.spacingXXXLarge))

            // Main Action Cards
            GameActionCard(
                title = "Create New Game",
                subtitle = "Start a fresh gaming session",
                icon = FeatherIcons.Plus,
                brush = GameHubThemeUtils.getPrimaryActionBrush(),
                onClick = { onEvent(MainScreenEvent.OnCreateRoom) },
                modifier = Modifier.fillMaxWidth(),
            )

            // Join Game Section
            JoinGameSection(
                roomId = state.searchedRoomId,
                onRoomIdChange = { onEvent(MainScreenEvent.OnSearchedRoomIdChanged(it)) },
                onJoinClick = { onEvent(MainScreenEvent.OnJoinRoom) },
                modifier = Modifier.fillMaxWidth(),
            )

            // My Games Section
            GameActionCard(
                title = "My Games",
                subtitle = "Continue your existing games",
                icon = FeatherIcons.User,
                brush = GameHubThemeUtils.getSecondaryActionBrush(),
                onClick = { onEvent(MainScreenEvent.OnSeeMyGames) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(GameHubDimensions.spacingHuge))
        }

        // Loading and Error Overlays
        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            LoadingCardOverlay()
        }

        AnimatedVisibility(
            visible = state.error != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            SimpleErrorCardOverlay(
                title = "Error",
                message = state.error,
                action = "OK" to { onEvent(MainScreenEvent.OnDialogClosed) },
            )
        }
    }
}

@Composable
private fun GameTitle() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GameHubDimensions.spacingSmall)
    ) {
        Text(
            text = "Game Hub",
            style = MaterialTheme.typography.displayLarge.copy(
                brush = GameHubThemeUtils.getTitleBrush()
            )
        )

        Text(
            text = "Your gaming adventure starts here",
            style = MaterialTheme.typography.bodyLarge,
            color = GameHubTheme.colors.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GameActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    brush: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 0.dp else GameHubDimensions.cardElevation,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    val offset by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val shape = RoundedCornerShape(GameHubShapes.cardCornerRadius)

    Box(
        modifier = modifier
            .offset(y = offset)
            .shadow(elevation = elevation, shape = shape)
            .clip(shape)
            .background(brush)
            .indication(interactionSource, LocalIndication.current)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)

                        isPressed = true
                        interactionSource.emit(press)

                        val released = tryAwaitRelease()

                        isPressed = false
                        interactionSource.emit(PressInteraction.Release(press))

                        if(released) onClick()
                    },
                )
            }
            .padding(GameHubDimensions.paddingLarge)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GameHubDimensions.spacingXLarge)
        ) {
            Box(
                modifier = Modifier
                    .size(GameHubShapes.avatarSizeMedium)
                    .background(GameHubTheme.colors.surfaceHighlight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GameHubTheme.colors.onPrimary,
                    modifier = Modifier.size(GameHubShapes.iconSizeLarge)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GameHubTheme.colors.onPrimaryVariant
                )
            }

            Icon(
                imageVector = FeatherIcons.ChevronRight,
                contentDescription = null,
                tint = GameHubTheme.colors.onPrimaryVariant,
                modifier = Modifier.size(GameHubShapes.iconSizeMedium)
            )
        }
    }
}

@Composable
private fun JoinGameSection(
    roomId: String,
    onRoomIdChange: (String) -> Unit,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val elevation = GameHubDimensions.surfaceElevation
    val shape = RoundedCornerShape(GameHubShapes.cardCornerRadius)
    val backgroundColor = Color(red = 47, green = 60, blue = 89) //todo

    Column(
        modifier = modifier
            .shadow(elevation, shape)
            .clip(shape)
            .background(backgroundColor)
            .padding(GameHubDimensions.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(GameHubDimensions.spacingLarge)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(GameHubDimensions.spacingMedium)
        ) {
            Box(
                modifier = Modifier
                    .size(GameHubShapes.avatarSizeSmall)
                    .background(
                        GameHubTheme.colors.accentSoft,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FeatherIcons.LogIn,
                    contentDescription = null,
                    tint = GameHubTheme.colors.accent,
                    modifier = Modifier.size(GameHubShapes.iconSizeSmall)
                )
            }

            Text(
                text = "Join Existing Game",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(GameHubDimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = roomId,
                onValueChange = onRoomIdChange,
                placeholder = {
                    Text(
                        text = "Enter Game ID",
                        color = GameHubTheme.colors.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GameHubTheme.colors.borderFocused,
                    unfocusedBorderColor = GameHubTheme.colors.border,
                    focusedTextColor = GameHubTheme.colors.onPrimary,
                    unfocusedTextColor = GameHubTheme.colors.onPrimary,
                    cursorColor = GameHubTheme.colors.accent
                ),
                shape = RoundedCornerShape(GameHubShapes.textFieldCornerRadius),
                singleLine = true
            )

            Button(
                onClick = onJoinClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GameHubTheme.colors.accent
                ),
                shape = RoundedCornerShape(GameHubShapes.buttonCornerRadius),
                modifier = Modifier.height(GameHubDimensions.buttonHeight)
            ) {
                Text(
                    text = "Join",
                    color = GameHubTheme.colors.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

private class MainScreenStatePreview: PreviewParameterProvider<MainScreenState> {
    override val values: Sequence<MainScreenState>
        get() = sequenceOf(
            MainScreenState(isLoading = false, error = null),
            MainScreenState(isLoading = true, error = null),
            MainScreenState(isLoading = false, error = "Error"),
        )
}

@Preview
@Composable
private fun MainScreenContentPreview(
    @PreviewParameter(MainScreenStatePreview::class)
    state: MainScreenState
) {
    GameHubTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreenContent(
                state = state,
                onEvent = {},
            )
        }
    }
}