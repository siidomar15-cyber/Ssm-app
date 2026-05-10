package com.ssm.study.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2563EB),
    secondary = Color(0xFF0891B2),
    tertiary = Color(0xFF7C3AED),
    surface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFFE2E8F0),
    background = Color(0xFFF1F5F9)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF93C5FD),
    secondary = Color(0xFF67E8F9),
    tertiary = Color(0xFFC4B5FD),
    surface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFF1E293B),
    background = Color(0xFF020617)
)

@Composable
fun SsmTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors: ColorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, typography = MaterialTheme.typography, content = content)
}
