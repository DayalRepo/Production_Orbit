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
    /**
     * Brand accent for icons: the one tint that is neither ink nor grey.
     *
     * Reserved for icons that are *doing* something rather than labelling something — a selected tab,
     * an active filter, an interactive link glyph. Spending it anywhere else costs it its meaning,
     * because an accent only reads as "this one" while it is rare on the screen.
     */
    val iconAccent: Color,
    /**
     * Glyph on a filled brand or action container. White on both themes, and deliberately not
     * [iconPrimary] inverted: the container underneath is the same blue in light and dark, so the
     * glyph on it has no reason to change with the theme.
     */
    val iconOnColor: Color,
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
 * The three text emphasis tiers, from the platform colour spec.
 *
 * Primary carries Display, H1 and H2 — the lines that establish hierarchy the moment a screen opens,
 * so they take the highest contrast available. Secondary carries Subheading and Body: a step down,
 * because a full paragraph at primary contrast is tiring to read for any length. Tertiary carries
 * captions, timestamps and hints, where the job is to be available without competing.
 *
 * ### Where the light theme departs from the spec, and why
 *
 * The spec sets tertiary to #8E8E93 on both themes. That measures 5.74:1 against the dark app
 * background and passes comfortably, but only **3.26:1** against the light one — short of the 4.5:1
 * WCAG 1.4.3 floor for normal-size text. It is a legal colour for large text or for a non-text UI
 * element, and it is the value Apple itself uses for `tertiaryLabel`, but our tertiary tier is the
 * caption tier and captions are normal-size text.
 *
 * So the light theme takes the same hue pulled down to #727276, which measures 4.56:1 against the
 * #F9F9FB app background and 4.79:1 against a white card — the lightest shade on that hue that
 * clears the floor on both surfaces. The dark theme takes the spec value unchanged.
 */

internal val AndroidLightContentColors = OrbitContentColors(
    textPrimary = Color(0xFF1C1C1E),   // Dark slate  · 17.01:1 on white
    textSecondary = Color(0xFF48484A), // Charcoal    ·  9.12:1
    textTertiary = Color(0xFF727276),  // Mid grey    ·  4.79:1 — see the note above
    textDisabled = Color(0xFFA8A9AA),  //             ·  2.35:1 — exempt
    iconPrimary = Color(0xFF1C1C1E),   // matches textPrimary
    iconAccent = Color(0xFF007AFF),    // Brand blue
    iconOnColor = Color(0xFFFFFFFF),
    iconInactive = Color(0xFF8E8E93),  //  3.26:1 — clears the 3:1 graphical floor
    iconDisabled = Color(0xFFC7C7CC),  //  1.61:1 — exempt
    avatarBorder = Color(0xFFE0E0E0),
    referenceSurface = Color(0xFFFFFFFF),
)

internal val AndroidDarkContentColors = OrbitContentColors(
    textPrimary = Color(0xFFF2F2F7),   // Off-white   · 16.77:1 on the app background
    textSecondary = Color(0xFFC7C7CC), // Light grey  · 11.11:1
    textTertiary = Color(0xFF8E8E93),  // Mid grey    ·  5.74:1
    textDisabled = Color(0xFF616163),  //             ·  3.03:1 — exempt
    iconPrimary = Color(0xFFF2F2F7),   // matches textPrimary
    iconAccent = Color(0xFF0A84FF),    // Brand blue
    iconOnColor = Color(0xFFFFFFFF),
    iconInactive = Color(0xFF8E8E93),  //  5.74:1
    iconDisabled = Color(0xFF48484A),  //  1.94:1 — exempt
    avatarBorder = Color(0xFF2E2E2E),
    referenceSurface = Color(0xFF121214),
)

internal val IosLightContentColors = OrbitContentColors(
    textPrimary = Color(0xFF1C1C1E),   // Dark slate  · 17.01:1 on white
    textSecondary = Color(0xFF48484A), // Charcoal    ·  9.12:1
    textTertiary = Color(0xFF727276),  // Mid grey    ·  4.79:1 — see the note above
    textDisabled = Color(0xFFA8A9AA),  //             ·  2.35:1 — exempt
    iconPrimary = Color(0xFF1C1C1E),   // matches textPrimary
    iconAccent = Color(0xFF007AFF),    // Brand blue
    iconOnColor = Color(0xFFFFFFFF),
    iconInactive = Color(0xFF8E8E93),  //  3.26:1 — clears the 3:1 graphical floor
    iconDisabled = Color(0xFFC7C7CC),  //  1.61:1 — exempt
    avatarBorder = Color(0xFFE0E0E0),
    referenceSurface = Color(0xFFFFFFFF),
)

internal val IosDarkContentColors = OrbitContentColors(
    textPrimary = Color(0xFFF2F2F7),   // Off-white   · 16.77:1 on the app background
    textSecondary = Color(0xFFC7C7CC), // Light grey  · 11.11:1
    textTertiary = Color(0xFF8E8E93),  // Mid grey    ·  5.74:1
    textDisabled = Color(0xFF616163),  //             ·  3.03:1 — exempt
    iconPrimary = Color(0xFFF2F2F7),   // matches textPrimary
    iconAccent = Color(0xFF0A84FF),    // Brand blue
    iconOnColor = Color(0xFFFFFFFF),
    iconInactive = Color(0xFF8E8E93),  //  5.74:1
    iconDisabled = Color(0xFF48484A),  //  1.94:1 — exempt
    avatarBorder = Color(0xFF2E2E2E),
    referenceSurface = Color(0xFF121214),
)

internal val LocalOrbitContentColors = staticCompositionLocalOf { AndroidLightContentColors }
