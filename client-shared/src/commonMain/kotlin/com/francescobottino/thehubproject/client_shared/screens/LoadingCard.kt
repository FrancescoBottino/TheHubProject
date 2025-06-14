package com.francescobottino.thehubproject.client_shared.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun LoadingCard(
    text: String = "Loading...",
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Column (
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .wrapContentSize()
            .shadow(elevation = 8.dp, shape = shape, clip = true)
            .clip(shape = shape)
            .background(color = MaterialTheme.colorScheme.surface, shape = shape)
            .padding(vertical = 16.dp, horizontal = 32.dp),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(60.dp),
        )

        Text(text = text)
    }
}