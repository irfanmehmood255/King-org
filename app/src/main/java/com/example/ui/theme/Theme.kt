package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val ArenaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

private val ArenaLightColorScheme = lightColorScheme(
    primary = ArenaBrandEmerald,
    onPrimary = ArenaCreamLight,
    primaryContainer = ArenaMintSoft,
    onPrimaryContainer = ArenaEmeraldDark,
    secondary = ArenaGoldPrimary,
    onSecondary = ArenaEmeraldDeep,
    secondaryContainer = ArenaGoldLight,
    onSecondaryContainer = ArenaEmeraldDeep,
    tertiary = ArenaEmeraldVibrant,
    onTertiary = ArenaSurfaceElevated,
    background = ArenaSoftCreamSurface,
    onBackground = ArenaCharcoalInk,
    surface = ArenaSurfaceElevated,
    onSurface = ArenaCharcoalInk,
    surfaceVariant = ArenaMintSoft,
    onSurfaceVariant = ArenaTextSecondary,
    surfaceContainerLowest = ArenaSurfaceElevated,
    surfaceContainerLow = ArenaSoftCreamSurface,
    surfaceContainer = ArenaInputBg,
    surfaceContainerHigh = ArenaMintSoft,
    outline = ArenaMintBorder,
    error = ArenaError,
    onError = ArenaCreamLight
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArenaLightColorScheme,
        typography = Typography,
        shapes = ArenaShapes,
        content = content
    )
}
