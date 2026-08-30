package com.orbitai.erp.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Raw palette. Never reference these directly from feature code; go through
 * [OrbitTheme.colorScheme] or [OrbitTheme.semanticColors] so light/dark and future
 * white-labelling keep working.
 */
internal object OrbitPalette {
    // Engineering blue — primary brand ramp.
    val Blue10 = Color(0xFF001B33)
    val Blue20 = Color(0xFF002F52)
    val Blue30 = Color(0xFF0B4472)
    val Blue40 = Color(0xFF0F4C81)
    val Blue60 = Color(0xFF4A87BE)
    val Blue80 = Color(0xFFA5C8E8)
    val Blue90 = Color(0xFFD3E4F6)
    val Blue95 = Color(0xFFE9F1FB)

    // Slate — secondary, used for structural chrome and metadata.
    val Slate10 = Color(0xFF141B22)
    val Slate20 = Color(0xFF29323C)
    val Slate30 = Color(0xFF3D4855)
    val Slate40 = Color(0xFF4A5A6A)
    val Slate60 = Color(0xFF7C8B9A)
    val Slate80 = Color(0xFFBFC9D4)
    val Slate90 = Color(0xFFDCE3EA)
    val Slate95 = Color(0xFFEEF2F6)

    // Safety amber — tertiary accent, high-visibility calls to action.
    val Amber10 = Color(0xFF2B1A00)
    val Amber20 = Color(0xFF472D00)
    val Amber30 = Color(0xFF6A4300)
    val Amber40 = Color(0xFF8F5B00)
    val Amber60 = Color(0xFFD98A05)
    val Amber70 = Color(0xFFF59E0B)
    val Amber80 = Color(0xFFFFC46B)
    val Amber90 = Color(0xFFFFE3B8)

    /*
     * Surface ramps.
     *
     * True neutral greys, not the blue-tinted ones used elsewhere in this palette: the dark base is
     * #121212, and tinting the elevated containers away from it makes the tonal steps read as a
     * colour cast rather than as elevation. Neither ramp reaches #000000 or, for text, #FFFFFF.
     */
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceLow = Color(0xFFFAFAFA)
    val LightSurfaceContainer = Color(0xFFF5F5F5)
    val LightSurfaceHigh = Color(0xFFEFEFEF)
    val LightSurfaceHighest = Color(0xFFE8E8E8)
    val LightSurfaceDim = Color(0xFFDEDEDE)

    val DarkSurfaceLowest = Color(0xFF0D0D0D)
    val DarkSurface = Color(0xFF121212)
    val DarkSurfaceLow = Color(0xFF171717)
    val DarkSurfaceContainer = Color(0xFF1D1D1D)
    val DarkSurfaceHigh = Color(0xFF232323)
    val DarkSurfaceHighest = Color(0xFF2A2A2A)
    val DarkSurfaceBright = Color(0xFF333333)

    // Neutrals.
    val Neutral0 = Color(0xFF000000)
    val Neutral6 = Color(0xFF0E1114)
    val Neutral10 = Color(0xFF16191D)
    val Neutral12 = Color(0xFF1B1F24)
    val Neutral17 = Color(0xFF25292F)
    val Neutral20 = Color(0xFF2B3037)
    val Neutral22 = Color(0xFF31363D)
    val Neutral30 = Color(0xFF434A52)
    val Neutral50 = Color(0xFF71797F)
    val Neutral60 = Color(0xFF8B939A)
    val Neutral80 = Color(0xFFC6CDD3)
    val Neutral87 = Color(0xFFDBE1E6)
    val Neutral90 = Color(0xFFE3E8ED)
    val Neutral94 = Color(0xFFEFF2F5)
    val Neutral96 = Color(0xFFF5F7F9)
    val Neutral98 = Color(0xFFFAFBFC)
    val Neutral100 = Color(0xFFFFFFFF)

    // Feedback ramps.
    val Red30 = Color(0xFF7F1D1D)
    val Red40 = Color(0xFFB3261E)
    val Red70 = Color(0xFFEF4444)
    val Red80 = Color(0xFFF2B8B5)
    val Red90 = Color(0xFFFCE8E6)

    val Green30 = Color(0xFF14532D)
    val Green40 = Color(0xFF15803D)
    val Green70 = Color(0xFF22C55E)
    val Green80 = Color(0xFF86EFAC)
    val Green90 = Color(0xFFDCFCE7)

    val Orange30 = Color(0xFF7C2D12)
    val Orange40 = Color(0xFFC2410C)
    val Orange70 = Color(0xFFFB923C)
    val Orange90 = Color(0xFFFFEDD5)

    val Violet30 = Color(0xFF4C1D95)
    val Violet40 = Color(0xFF6D28D9)
    val Violet70 = Color(0xFFA78BFA)
    val Violet90 = Color(0xFFEDE9FE)

    val Teal30 = Color(0xFF134E4A)
    val Teal40 = Color(0xFF0F766E)
    val Teal70 = Color(0xFF2DD4BF)
    val Teal90 = Color(0xFFCCFBF1)
}

/**
 * Text and surface roles come from [content] so the platform's charcoal or off-white primary text
 * is the single source of truth. Brand and feedback roles are shared across platforms.
 */
internal fun orbitLightColorScheme(content: OrbitContentColors) = lightColorScheme(
    primary = OrbitPalette.Blue40,
    onPrimary = OrbitPalette.Neutral100,
    primaryContainer = OrbitPalette.Blue90,
    onPrimaryContainer = OrbitPalette.Blue10,
    inversePrimary = OrbitPalette.Blue80,

    secondary = OrbitPalette.Slate40,
    onSecondary = OrbitPalette.Neutral100,
    secondaryContainer = OrbitPalette.Slate90,
    onSecondaryContainer = OrbitPalette.Slate10,

    tertiary = OrbitPalette.Amber40,
    onTertiary = OrbitPalette.Neutral100,
    tertiaryContainer = OrbitPalette.Amber90,
    onTertiaryContainer = OrbitPalette.Amber10,

    background = OrbitPalette.LightSurface,
    onBackground = content.textPrimary,
    surface = OrbitPalette.LightSurface,
    onSurface = content.textPrimary,
    surfaceVariant = OrbitPalette.LightSurfaceHigh,
    onSurfaceVariant = content.textSecondary,
    surfaceTint = OrbitPalette.Blue40,

    surfaceDim = OrbitPalette.LightSurfaceDim,
    surfaceBright = OrbitPalette.LightSurface,
    surfaceContainerLowest = OrbitPalette.LightSurface,
    surfaceContainerLow = OrbitPalette.LightSurfaceLow,
    surfaceContainer = OrbitPalette.LightSurfaceContainer,
    surfaceContainerHigh = OrbitPalette.LightSurfaceHigh,
    surfaceContainerHighest = OrbitPalette.LightSurfaceHighest,

    inverseSurface = OrbitPalette.DarkSurfaceHigh,
    inverseOnSurface = OrbitPalette.LightSurfaceContainer,

    error = OrbitPalette.Red40,
    onError = OrbitPalette.Neutral100,
    errorContainer = OrbitPalette.Red90,
    onErrorContainer = OrbitPalette.Red30,

    outline = OrbitPalette.Neutral50,
    outlineVariant = OrbitPalette.Neutral80,
    scrim = OrbitPalette.Neutral0,
)

internal fun orbitDarkColorScheme(content: OrbitContentColors) = darkColorScheme(
    primary = OrbitPalette.Blue80,
    onPrimary = OrbitPalette.Blue20,
    primaryContainer = OrbitPalette.Blue30,
    onPrimaryContainer = OrbitPalette.Blue90,
    inversePrimary = OrbitPalette.Blue40,

    secondary = OrbitPalette.Slate80,
    onSecondary = OrbitPalette.Slate20,
    secondaryContainer = OrbitPalette.Slate30,
    onSecondaryContainer = OrbitPalette.Slate90,

    tertiary = OrbitPalette.Amber80,
    onTertiary = OrbitPalette.Amber20,
    tertiaryContainer = OrbitPalette.Amber30,
    onTertiaryContainer = OrbitPalette.Amber90,

    background = OrbitPalette.DarkSurface,
    onBackground = content.textPrimary,
    surface = OrbitPalette.DarkSurface,
    onSurface = content.textPrimary,
    surfaceVariant = OrbitPalette.DarkSurfaceHighest,
    onSurfaceVariant = content.textSecondary,
    surfaceTint = OrbitPalette.Blue80,

    surfaceDim = OrbitPalette.DarkSurfaceLowest,
    surfaceBright = OrbitPalette.DarkSurfaceBright,
    surfaceContainerLowest = OrbitPalette.DarkSurfaceLowest,
    surfaceContainerLow = OrbitPalette.DarkSurfaceLow,
    surfaceContainer = OrbitPalette.DarkSurfaceContainer,
    surfaceContainerHigh = OrbitPalette.DarkSurfaceHigh,
    surfaceContainerHighest = OrbitPalette.DarkSurfaceHighest,

    inverseSurface = OrbitPalette.LightSurfaceHighest,
    inverseOnSurface = OrbitPalette.DarkSurfaceHigh,

    error = OrbitPalette.Red80,
    onError = OrbitPalette.Red30,
    errorContainer = OrbitPalette.Red40,
    onErrorContainer = OrbitPalette.Red90,

    outline = OrbitPalette.Neutral60,
    outlineVariant = OrbitPalette.Neutral30,
    scrim = OrbitPalette.Neutral0,
)
