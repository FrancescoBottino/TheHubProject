package com.francescobottino.thehubproject.client_shared.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.francescobottino.thehubproject.client_shared.ui.images.MainIcon
import com.francescobottino.thehubproject.client_shared.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LogoBig(
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Image(
            imageVector = MainIcon,
            contentDescription = "logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.requiredSize(64.dp)
        )

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

@Preview
@Composable
fun LogoBigPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LogoBig()
        }
    }
}

@Composable
fun LogoSmall(
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Image(
            imageVector = MainIcon,
            contentDescription = "logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.requiredSize(32.dp)
        )

        VerticalDivider(modifier = Modifier.requiredHeight(32.dp))

        Text(
            text = "The Hub Project",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
fun LogoSmallPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LogoSmall()
        }
    }
}