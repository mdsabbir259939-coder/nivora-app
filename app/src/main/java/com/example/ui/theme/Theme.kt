package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val NivoraShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun NivoraTheme(
    paletteKey: String = NivoraPaletteKey.TEAL_BEIGE.key,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = NivoraPaletteKey.fromKey(paletteKey)
    val targetScheme = if (darkTheme) darkSchemeFor(palette) else lightSchemeFor(palette)

    val animatedScheme = animateColorScheme(targetScheme)

    MaterialTheme(
        colorScheme = animatedScheme,
        typography = NivoraTypography,
        shapes = NivoraShapes,
        content = content
    )
}

@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
    val primary by animateColorAsState(target.primary, tween(350), label = "primary")
    val onPrimary by animateColorAsState(target.onPrimary, tween(350), label = "onPrimary")
    val primaryContainer by animateColorAsState(target.primaryContainer, tween(350), label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(target.onPrimaryContainer, tween(350), label = "onPrimaryContainer")
    val secondary by animateColorAsState(target.secondary, tween(350), label = "secondary")
    val onSecondary by animateColorAsState(target.onSecondary, tween(350), label = "onSecondary")
    val secondaryContainer by animateColorAsState(target.secondaryContainer, tween(350), label = "secondaryContainer")
    val onSecondaryContainer by animateColorAsState(target.onSecondaryContainer, tween(350), label = "onSecondaryContainer")
    val background by animateColorAsState(target.background, tween(350), label = "background")
    val onBackground by animateColorAsState(target.onBackground, tween(350), label = "onBackground")
    val surface by animateColorAsState(target.surface, tween(350), label = "surface")
    val onSurface by animateColorAsState(target.onSurface, tween(350), label = "onSurface")
    val surfaceVariant by animateColorAsState(target.surfaceVariant, tween(350), label = "surfaceVariant")
    val onSurfaceVariant by animateColorAsState(target.onSurfaceVariant, tween(350), label = "onSurfaceVariant")
    val surfaceContainerLow by animateColorAsState(target.surfaceContainerLow, tween(350), label = "surfaceContainerLow")
    val surfaceContainer by animateColorAsState(target.surfaceContainer, tween(350), label = "surfaceContainer")
    val surfaceContainerHigh by animateColorAsState(target.surfaceContainerHigh, tween(350), label = "surfaceContainerHigh")
    val outline by animateColorAsState(target.outline, tween(350), label = "outline")

    return target.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        outline = outline
    )
}
