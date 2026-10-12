package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Color(0xFF090D16),
    primaryContainer = DarkBlueAccent,
    onPrimaryContainer = Color.White,
    secondary = GoldLight,
    onSecondary = Color(0xFF090D16),
    secondaryContainer = GoldDark,
    onSecondaryContainer = Color.White,
    tertiary = TieGreen,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = BackgroundSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun AngolaPredictTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
