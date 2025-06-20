package com.francescobottino.thehubproject.client_shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if(darkMode) {
            lightColorScheme()                      //TODO
        } else {
            darkColorScheme()                       //TODO
        },
        shapes = MaterialTheme.shapes,              //TODO
        typography = MaterialTheme.typography,      //TODO
        content = content,
    )
}