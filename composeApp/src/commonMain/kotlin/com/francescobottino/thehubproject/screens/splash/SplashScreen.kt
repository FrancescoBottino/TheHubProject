package com.francescobottino.thehubproject.screens.splash

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.francescobottino.thehubproject.client_shared.ui.components.SimpleErrorCardOverlay
import com.francescobottino.thehubproject.client_shared.ui.components.VerticalCenteredLayout
import com.francescobottino.thehubproject.client_shared.ui.images.MainIcon
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.time.Duration.Companion.seconds

private val animationTransitionDelay = 1.seconds.inWholeMilliseconds
private val animationTransitionDuration = (1).seconds.inWholeMilliseconds

private val animationSettleDownDuration = (0.2).seconds.inWholeMilliseconds

private val animationTotalDuration = animationTransitionDelay + animationTransitionDuration + animationSettleDownDuration

object SplashScreen: Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SplashScreenModel(navigator, animationTotalDuration) }

        val state by screenModel.state.collectAsState()

        Scaffold {
            SplashScreenContent(
                state = state,
                onEvent = screenModel::onEvent,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun SplashScreenContent(
    state: SplashScreenState,
    onEvent: (SplashScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        VerticalCenteredLayout(
            modifier = Modifier.fillMaxSize(),
            below = {
                if(!state.isError) {
                    Box(
                        modifier = Modifier.requiredSize(64.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        ) {
            LogoAnimated()
        }

        if(state.isError) {
            SimpleErrorCardOverlay(
                title = "Error",
                message = "There was an error while trying to fetch user data.",
                action = "Try Again" to { onEvent(SplashScreenEvent.TryAgain) },
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LogoAnimated(
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    val durationMillis = animationTransitionDuration.toInt()
    val imageBigSize = 120.dp
    val imageSmallSize = 64.dp

    val screenWidth = LocalWindowInfo.current.containerSize.width
    val screenWidthDp = with(LocalDensity.current) { screenWidth.toDp() }

    val initialTextOffset = -(screenWidth.toFloat())
    val finalTextOffset = 0f

    var textSize by remember { mutableStateOf<Dp?>(null) }

    LaunchedEffect(textSize, expanded) {
        if(textSize == null || expanded) return@LaunchedEffect

        delay(animationTransitionDelay)
        expanded = true
    }

    val textOffset by animateFloatAsState(
        targetValue = if(expanded) finalTextOffset else initialTextOffset,
        animationSpec = tween(durationMillis)
    )

    val textMaxSize by animateDpAsState(
        targetValue = if(expanded) textSize ?: Dp.Infinity else 0.dp,
        animationSpec = tween(durationMillis)
    )

    val imageSize by animateDpAsState(
        targetValue = if(expanded) imageSmallSize else imageBigSize,
        animationSpec = tween(durationMillis),
    )

    Box(
        modifier = modifier
            .requiredHeight(imageBigSize)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.requiredSize(imageSmallSize)
                )

                BoxWithConstraints(
                    modifier = Modifier
                        .requiredHeight(imageSmallSize)
                        .wrapContentWidth()
                        .clipToBounds()
                        .graphicsLayer { translationX = textOffset }
                ) {
                    textSize = maxWidth

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        VerticalDivider(modifier = Modifier.requiredHeight(64.dp))

                        Column {
                            Text(
                                text = "The Hub Project",
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                            )
                            Text(
                                text = "Your multiplayer games",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.requiredSize(imageSize)
                ) {
                    Image(
                        imageVector = MainIcon,
                        contentDescription = "logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.requiredSize(imageSize)
                    )
                }

                Box(
                    modifier = Modifier
                        .requiredHeight(imageSmallSize)
                        .requiredWidthIn(max = textMaxSize)
                        .wrapContentWidth()
                        .graphicsLayer { alpha = 0f }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        VerticalDivider(modifier = Modifier.requiredHeight(64.dp))

                        Column {
                            Text(
                                text = "The Hub Project",
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                            )
                            Text(
                                text = "Your multiplayer games",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

private class SplashScreenStatePreview: PreviewParameterProvider<SplashScreenState> {
    override val values = sequenceOf(
        SplashScreenState(isError = false),
        SplashScreenState(isError = true),
    )
}

@Preview
@Composable
private fun SplashScreenContentPreviewWithError(
    @PreviewParameter(SplashScreenStatePreview::class)
    state: SplashScreenState
) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            SplashScreenContent(state, {})
        }
    }
}