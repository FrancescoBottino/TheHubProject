package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,    //TODO
        shapes = MaterialTheme.shapes,              //TODO
        typography = MaterialTheme.typography,      //TODO
        content = content,
    )
}