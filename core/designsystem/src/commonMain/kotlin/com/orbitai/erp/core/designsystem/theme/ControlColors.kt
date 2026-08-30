package com.orbitai.erp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * The monochrome palette buttons and icon buttons are built from.
 *
 * Controls are deliberately neutral rather than brand-coloured. Colour in this product carries
 * meaning — a tone on a badge says "overdue", "blocked", "awaiting QA" — and if the buttons are
 * coloured too then a screen has two competing colour languages and neither reads. Black-and-white
 * controls leave the palette free to mean something.
 *
 * Two polarities are in play, and the difference is the point:
 *
 * - **[actionContainer] inverts against the theme.** A filled button is near-black on a light theme
 *   and near-white on a dark one. Inversion is what makes a filled button the loudest thing on the
 *   screen without spending a hue on it.
 * - **[controlContainer] stays with the theme.** An icon button's disc is a faint tint of the
 *   foreground over the current surface, so a toolbar of them recedes instead of reading as a row
 *   of holes punched in the page.
 *
 * Neither extreme is pure. `#FFFFFF` on `#000000` is what the house rules forbid for exactly the
 * reason it looks wrong here: at full contrast a filled button vibrates and its label blooms.
 *
 * `ControlContrastTest` verifies every pair in this file against the glass stack that gets drawn on
 * top of it, so these are not eyeballed values.
 */
@Immutable
data class OrbitControlColors(
    /** Filled-button container. Inverts against the theme. */
    val actionContainer: Color,
    /** Label and glyph on [actionContainer]. */
    val onActionContainer: Color,

    /**
     * Tonal container for icon buttons and the Secondary button. Translucent, so the glass reads
     * and so the same value works on any surface the control lands on.
     */
    val controlContainer: Color,
    /** Hairline ring around a tonal or outlined control. */
    val controlBorder: Color,
    /** Glyph or label on [controlContainer], and on no container at all. */
    val controlContent: Color,

    /**
     * Fill for an icon button's ring: white on light, grey on dark.
     *
     * The one value in this file that is neither an inversion nor a tint of the foreground. It is a
     * lens rather than a surface — translucent enough that whatever is behind still tints through,
     * opaque enough to lift the ring off the page — and it is deliberately achromatic so the coloured
     * glyph inside it has nothing to compete with.
     *
     * White on light is close to invisible on a white page, and that is expected rather than a bug:
     * the ring is separated there by its rim and its shadow, and the fill only starts doing visible
     * work once the control sits on a card, an image or any surface that is not the page itself —
     * which is where icon buttons actually live. The alternative, a grey fill on light, makes every
     * toolbar look disabled.
     */
    val ringContainer: Color,

    /**
     * Ring for the Outline variant.
     *
     * Stronger than [controlBorder] because on an Outline control the ring is the only thing
     * defining the target, whereas on a tonal one the fill already does that.
     */
    val outlineBorder: Color,
)

/**
 * Light theme.
 *
 * The filled container is `#1A1C1E`, the same deep charcoal the house rules specify for primary
 * text, rather than black — see the class doc.
 */
internal val OrbitLightControlColors = OrbitControlColors(
    actionContainer = Color(0xFF1A1C1E),
    onActionContainer = Color(0xFFF7F7F8),
    controlContainer = Color(0x141A1C1E),
    controlBorder = Color(0x241A1C1E),
    controlContent = Color(0xFF1A1C1E),
    ringContainer = Color(0xB8FFFFFF),
    outlineBorder = Color(0x471A1C1E),
)

/** Dark theme, with the polarity of [actionContainer] flipped. */
internal val OrbitDarkControlColors = OrbitControlColors(
    actionContainer = Color(0xFFE3E2E6),
    onActionContainer = Color(0xFF16191D),
    controlContainer = Color(0x1FE3E2E6),
    controlBorder = Color(0x33E3E2E6),
    controlContent = Color(0xFFE3E2E6),
    // Grey rather than a tint of the off-white foreground: on a near-black page a white-derived
    // translucent fill picks up the foreground's slight blue and reads as cold, where a plain grey
    // reads as glass.
    ringContainer = Color(0x2EA8A8AC),
    outlineBorder = Color(0x5CE3E2E6),
)

val OrbitTheme.controlColors: OrbitControlColors
    @Composable @ReadOnlyComposable get() =
        if (isDark) OrbitDarkControlColors else OrbitLightControlColors
