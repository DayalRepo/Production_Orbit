package com.orbitai.erp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.orbitai.erp.core.designsystem.foundation.ProvideWindowSize

private val LocalIsDarkTheme = staticCompositionLocalOf { false }

/**
 * Single entry point for OrbitAI styling. Wraps [MaterialTheme] and additionally provides the
 * design tokens Material does not model: semantic status colours, a spacing/sizing scale,
 * component shape roles, elevation roles, extended typography and the current window size.
 *
 * Always read tokens through this object rather than [MaterialTheme] directly.
 */
@Composable
fun OrbitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) OrbitDarkColorScheme else OrbitLightColorScheme
    val semanticColors = if (darkTheme) OrbitDarkSemanticColors else OrbitLightSemanticColors

    CompositionLocalProvider(
        LocalIsDarkTheme provides darkTheme,
        LocalOrbitSemanticColors provides semanticColors,
        LocalOrbitSpacing provides OrbitSpacing(),
        LocalOrbitSizing provides OrbitSizing(),
        LocalOrbitShapes provides OrbitShapeTokens(),
        LocalOrbitElevation provides OrbitElevation(),
        LocalOrbitTypography provides OrbitTypographyTokens(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = OrbitTypography,
            shapes = OrbitShapes,
        ) {
            ProvideWindowSize(content = content)
        }
    }
}

object OrbitTheme {
    val colorScheme: ColorScheme
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable @ReadOnlyComposable get() = MaterialTheme.typography

    val shapes: Shapes
        @Composable @ReadOnlyComposable get() = MaterialTheme.shapes

    val semanticColors: OrbitSemanticColors
        @Composable @ReadOnlyComposable get() = LocalOrbitSemanticColors.current

    val spacing: OrbitSpacing
        @Composable @ReadOnlyComposable get() = LocalOrbitSpacing.current

    val sizing: OrbitSizing
        @Composable @ReadOnlyComposable get() = LocalOrbitSizing.current

    val shapeTokens: OrbitShapeTokens
        @Composable @ReadOnlyComposable get() = LocalOrbitShapes.current

    val elevation: OrbitElevation
        @Composable @ReadOnlyComposable get() = LocalOrbitElevation.current

    val extendedTypography: OrbitTypographyTokens
        @Composable @ReadOnlyComposable get() = LocalOrbitTypography.current

    val isDark: Boolean
        @Composable @ReadOnlyComposable get() = LocalIsDarkTheme.current
}
