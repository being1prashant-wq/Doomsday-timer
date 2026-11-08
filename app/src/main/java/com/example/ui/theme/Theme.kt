package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DoomsdayDarkColorScheme = darkColorScheme(
    primary = SilverPrimary,
    onPrimary = ObsidianBlack,
    secondary = SilverAccent,
    onSecondary = ObsidianBlack,
    tertiary = SilverMuted,
    background = ObsidianBlack,
    onBackground = SilverPrimary,
    surface = ObsidianCharcoal,
    onSurface = SilverPrimary,
    surfaceVariant = GlassSurfaceDark,
    onSurfaceVariant = SilverMuted,
    outline = SpecularBorder,
    error = NegativeCrimson
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DoomsdayDarkColorScheme,
        typography = Typography,
        content = content
    )
}
