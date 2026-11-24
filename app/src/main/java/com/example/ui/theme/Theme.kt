package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PakTokDarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = ObsidianBlack,
    primaryContainer = DeepBronzeGold,
    onPrimaryContainer = BrightGold,
    secondary = CoinYellow,
    onSecondary = ObsidianBlack,
    secondaryContainer = SlateObsidian,
    onSecondaryContainer = CoinYellowLight,
    tertiary = Gold24K,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = IvoryText,
    surface = CarbonBlack,
    onSurface = IvoryText,
    surfaceVariant = SlateObsidian,
    onSurfaceVariant = ChampagneMuted,
    outline = GoldHairline,
    outlineVariant = ElevatedCharcoal,
    error = HeartRedGold,
    onError = Color.White
)

@Composable
fun PakTokTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PakTokDarkColorScheme,
        typography = PakTokTypography,
        content = content
    )
}
