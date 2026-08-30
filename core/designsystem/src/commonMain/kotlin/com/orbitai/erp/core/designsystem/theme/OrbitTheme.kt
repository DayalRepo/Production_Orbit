package com.orbitai.erp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import com.orbitai.erp.core.designsystem.foundation.ProvideWindowSize
import com.orbitai.erp.core.designsystem.foundation.currentPlatform

private val LocalIsDarkTheme = staticCompositionLocalOf { false }
private val LocalOrbitPlatformTokens = staticCompositionLocalOf { AndroidPlatformTokens }

/**
 * Single entry point for OrbitAI styling. Wraps [MaterialTheme] and additionally provides the
 * design tokens Material does not model: semantic status colours, text/icon content colours, a
 * spacing and sizing scale, component shape roles, elevation roles, extended typography and the
 * current window size.
 *
 * Type scale, text colour, icon and avatar sizing, minimum touch target and top-bar title alignment
 * all resolve from the running platform. Always read tokens through this object rather than
 * [MaterialTheme] directly.
 *
 * @param platform overridable so previews and tests can render the iOS token set from a JVM host.
 */
@Composable
fun OrbitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    platform: OrbitPlatform = currentPlatform,
    content: @Composable () -> Unit,
) {
    val tokens = remember(platform) { platformTokens(platform) }
    val contentColors = if (darkTheme) tokens.darkContentColors else tokens.lightContentColors
    val colorScheme = remember(darkTheme, contentColors) {
        if (darkTheme) orbitDarkColorScheme(contentColors) else orbitLightColorScheme(contentColors)
    }
    val semanticColors = if (darkTheme) OrbitDarkSemanticColors else OrbitLightSemanticColors

    val sans = orbitFontFamily()
    val typography = remember(sans, tokens) { orbitTypography(sans, tokens.typeScale) }
    val extendedTypography = remember(sans, tokens) {
        orbitTypographyTokens(sans, tokens.typeScale)
    }

    CompositionLocalProvider(
        LocalIsDarkTheme provides darkTheme,
        LocalOrbitPlatformTokens provides tokens,
        LocalOrbitContentColors provides contentColors,
        LocalOrbitSemanticColors provides semanticColors,
        LocalOrbitSpacing provides OrbitSpacing(),
        LocalOrbitSizing provides tokens.sizing,
        LocalOrbitShapes provides OrbitShapeTokens(),
        LocalOrbitElevation provides OrbitElevation(),
        LocalOrbitTypography provides extendedTypography,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
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

    /** Text, icon and avatar-edge colours with verified contrast. */
    val contentColors: OrbitContentColors
        @Composable @ReadOnlyComposable get() = LocalOrbitContentColors.current

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

    val typeScale: OrbitTypeScale
        @Composable @ReadOnlyComposable get() = LocalOrbitPlatformTokens.current.typeScale

    val platform: OrbitPlatform
        @Composable @ReadOnlyComposable get() = LocalOrbitPlatformTokens.current.platform

    /** Start on Android, Center on iOS. Read this instead of hardcoding a top-bar alignment. */
    val topBarTitleAlignment: OrbitTitleAlignment
        @Composable @ReadOnlyComposable get() =
            LocalOrbitPlatformTokens.current.topBarTitleAlignment

    val isDark: Boolean
        @Composable @ReadOnlyComposable get() = LocalIsDarkTheme.current
}
