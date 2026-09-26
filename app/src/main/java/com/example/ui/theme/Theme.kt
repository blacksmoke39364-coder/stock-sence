package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val StockSenseColorScheme = lightColorScheme(
    primary = JewelTeal,
    onPrimary = ClayHighlight,
    primaryContainer = ClaySurfaceAlt,
    onPrimaryContainer = ForestInk,
    secondary = JewelNavy,
    onSecondary = ClayHighlight,
    secondaryContainer = ClaySurface,
    onSecondaryContainer = ForestInk,
    tertiary = JewelBrass,
    onTertiary = ForestInk,
    background = DustySageGround,
    onBackground = ForestInk,
    surface = ClaySurface,
    onSurface = ForestInk,
    surfaceVariant = ClaySurfaceAlt,
    onSurfaceVariant = MutedSageGrey,
    outline = SubtleBorder,
    error = JewelRust,
    onError = ClayHighlight
)

@Composable
fun StockSenseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StockSenseColorScheme,
        typography = Typography,
        content = content
    )
}
