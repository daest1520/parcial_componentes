package com.periodista.casos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresClaros = lightColorScheme(
    primary = Color(0xFF263238),
    onPrimary = Color.White,
    secondary = Color(0xFFB71C1C),
    onSecondary = Color.White,
    primaryContainer = Color(0xFFCFD8DC),
    onPrimaryContainer = Color(0xFF102027)
)

private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFFB0BEC5),
    onPrimary = Color(0xFF102027),
    secondary = Color(0xFFEF9A9A),
    primaryContainer = Color(0xFF37474F),
    onPrimaryContainer = Color(0xFFECEFF1)
)

@Composable
fun CasosTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) ColoresOscuros else ColoresClaros,
        content = content
    )
}
