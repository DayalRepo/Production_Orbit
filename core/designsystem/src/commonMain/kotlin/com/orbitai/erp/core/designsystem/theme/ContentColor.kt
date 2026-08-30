package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Text, icon and avatar-edge colours, per platform and per theme.
 *
 * These are pre-composited flat colours rather than a base colour plus an alpha. Alpha is only
 * predictable over a known background, and a translucent label dropped onto a status container or a
 * photo drifts out of compliance silently. Every value here has a measured contrast ratio against
 * its own surface, asserted in `ContrastTest`.
 *
 * Two rules the spec sheets are built on:
 * - Light theme never puts `#000000` on `#FFFFFF`. 21:1 causes visual vibration and eye strain.
 * - Dark theme never puts `#FFFFFF` on `#000000`. It blooms (halation) during long reading.
 */
@Immutable
data class OrbitContentColors(
    /** Body and heading text. */
    val textPrimary: Color,
    /** Captions and subtext. 70% of primary, and still AA for normal text. */
    val textSecondary: Color,
    /** Lowest readable tier. 65%, not 60% — see the note below. */
    val textTertiary: Color,
    /** Disabled labels. Below 4.5:1 by design; WCAG 1.4.3 exempts disabled controls. */
    val textDisabled: Color,

    val iconPrimary: Color,
    /** Inactive tab and toolbar icons. 60%, holding the 3:1 floor of WCAG 1.4.11. */
    val iconInactive: Color,
    val iconDisabled: Color,

    /**
     * Hairline drawn around avatars so a photo with dark edges does not bleed into a dark container,
     * and one with light edges does not bleed into a white card.
     */
    val avatarBorder: Color,

    /** The surface these values were measured against. Used by the contrast test. */
    val referenceSurface: Color,
)

/*
 * Why textTertiary is 65% and not the 60% the brief allows:
 *
 * 60% of #1A1C1E over #FFFFFF is #767778, which measures 4.49:1 — it misses the 4.5:1 requirement
 * for normal text by one hundredth. 60% is safe in the dark theme (5.80:1) but not the light one, so
 * the tier is 65% on both to keep one number in the design system rather than two.
 */

internal val AndroidLightContentColors = OrbitContentColors(
    textPrimary = Color(0xFF1A1C1E),   // 17.09:1
    textSecondary = Color(0xFF5F6062), //  6.29:1
    textTertiary = Color(0xFF6A6B6D),  //  5.33:1
    textDisabled = Color(0xFFA8A9AA),  //  2.35:1 — exempt
    iconPrimary = Color(0xFF1F1F1F),   // 16.48:1
    iconInactive = Color(0xFF797979),  //  4.35:1
    iconDisabled = Color(0xFFAAAAAA),  //  2.32:1 — exempt
    avatarBorder = Color(0xFFE0E0E0),
    referenceSurface = Color(0xFFFFFFFF),
)

internal val AndroidDarkContentColors = OrbitContentColors(
    textPrimary = Color(0xFFE3E2E6),   // 14.53:1
    textSecondary = Color(0xFFA4A4A6), //  7.53:1
    textTertiary = Color(0xFF9A999C),  //  6.61:1
    textDisabled = Color(0xFF616163),  //  3.03:1 — exempt
    iconPrimary = Color(0xFFE3E2E6),   // 14.53:1
    iconInactive = Color(0xFF8F8F91),  //  5.80:1
    iconDisabled = Color(0xFF616163),  //  3.03:1 — exempt
    avatarBorder = Color(0xFF2E2E2E),
    referenceSurface = Color(0xFF121212),
)

internal val IosLightContentColors = OrbitContentColors(
    textPrimary = Color(0xFF1C1C1E),   // 17.01:1
    textSecondary = Color(0xFF606062), //  6.27:1
    textTertiary = Color(0xFF6B6B6D),  //  5.32:1
    textDisabled = Color(0xFFA9A9AA),  //  2.35:1 — exempt
    iconPrimary = Color(0xFF3A3A3C),   // 11.35:1
    iconInactive = Color(0xFF89898A),  //  3.49:1
    iconDisabled = Color(0xFFB4B4B5),  //  2.07:1 — exempt
    avatarBorder = Color(0xFFE0E0E0),
    referenceSurface = Color(0xFFFFFFFF),
)

internal val IosDarkContentColors = OrbitContentColors(
    textPrimary = Color(0xFFEBEBF5),   // 15.82:1
    textSecondary = Color(0xFFAAAAB1), //  8.11:1
    textTertiary = Color(0xFF9F9FA6),  //  7.12:1
    textDisabled = Color(0xFF646468),  //  3.18:1 — exempt
    iconPrimary = Color(0xFFEBEBF5),   // 15.82:1
    iconInactive = Color(0xFF94949A),  //  6.21:1
    iconDisabled = Color(0xFF646468),  //  3.18:1 — exempt
    avatarBorder = Color(0xFF2E2E2E),
    referenceSurface = Color(0xFF121212),
)

internal val LocalOrbitContentColors = staticCompositionLocalOf { AndroidLightContentColors }
