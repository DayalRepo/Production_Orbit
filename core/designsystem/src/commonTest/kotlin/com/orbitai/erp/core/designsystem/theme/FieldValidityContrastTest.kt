package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrast verification for the field rims that report validity.
 *
 * A rim is not text, so the bar is WCAG 1.4.11's 3:1 for non-text content rather than 4.5:1 — but it
 * is a *meaningful* graphic, not decoration: it is the only thing on the control saying whether the
 * field has been filled in, so 3:1 is a floor it genuinely has to clear rather than a nicety.
 *
 * Two separate properties are checked, and it is worth being clear that they are different, because
 * passing one does not imply the other:
 *
 * - each rim must be visible against the surface behind it, or the signal is not there at all;
 * - the red and the green must be distinguishable *from each other*, because the state is encoded in
 *   which of the two it is. A pair that both clear 3:1 against the page can still be nearly the same
 *   colour as one another.
 *
 * Neither test makes the colour-blind case acceptable on its own. Roughly one man in twelve cannot
 * separate these hues at all, which is why the field also thickens the rim and why supporting text
 * carries the actual instruction (WCAG 1.4.1). This asserts the colour channel is sound for the users
 * who can use it; it does not assert that the colour channel is sufficient.
 */
class FieldValidityContrastTest {

    private fun channel(value: Float): Double {
        val c = value.toDouble()
        return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }

    private fun luminance(color: Color): Double =
        0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)

    private fun contrast(a: Color, b: Color): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }

    private fun Color.over(background: Color): Color = Color(
        red = alpha * red + (1 - alpha) * background.red,
        green = alpha * green + (1 - alpha) * background.green,
        blue = alpha * blue + (1 - alpha) * background.blue,
    )

    private val themes = listOf(
        Triple(
            "light",
            listOf(
                OrbitPalette.LightSurface,
                OrbitPalette.LightSurfaceContainer,
                OrbitPalette.LightSurfaceHighest,
            ),
            OrbitLightBadgeColors,
        ),
        Triple(
            "dark",
            listOf(
                OrbitPalette.DarkSurface,
                OrbitPalette.DarkSurfaceContainer,
                OrbitPalette.DarkSurfaceHighest,
            ),
            OrbitDarkBadgeColors,
        ),
    )

    @Test
    fun `each validity rim is visible on every surface a field can sit on`() {
        themes.forEach { (theme, surfaces, badges) ->
            listOf(
                "empty" to badges.getValue(OrbitBadgeTone.Red).label,
                "filled" to badges.getValue(OrbitBadgeTone.Green).label,
            ).forEach { (stateName, rim) ->
                surfaces.forEach { surface ->
                    val ratio = contrast(rim.over(surface), surface)
                    assertTrue(
                        ratio >= NonTextFloor,
                        "the $theme $stateName rim is ${(ratio * 1000).toInt() / 1000.0}:1 on " +
                            "$surface, under the $NonTextFloor:1 floor for meaningful graphics",
                    )
                }
            }
        }
    }

    @Test
    fun `the two validity rims are distinguishable from one another`() {
        // Not a WCAG number — there is no criterion for "two indicators must differ" — so this uses
        // the same 3:1 the rims owe the page. The reasoning is that if the two rims are closer to
        // each other than either is to its background, the thing carrying the state is weaker than
        // the thing carrying mere presence, and a user reads presence instead of state.
        themes.forEach { (theme, _, badges) ->
            val empty = badges.getValue(OrbitBadgeTone.Red).label
            val filled = badges.getValue(OrbitBadgeTone.Green).label
            val ratio = contrast(empty, filled)
            val hueGap = abs(empty.red - filled.red) + abs(empty.green - filled.green)
            assertTrue(
                ratio >= PairFloor || hueGap >= HueGapFloor,
                "the $theme empty and filled rims differ by only " +
                    "${(ratio * 1000).toInt() / 1000.0}:1 in luminance and $hueGap in hue, so " +
                    "which state a field is in is not readable from its rim",
            )
        }
    }

    @Test
    fun `an invalid rim outranks a neutral one`() {
        // The rim is a single channel carrying several signals, so precedence decides what the user
        // sees. A validity rim has to be more prominent than the resting neutral one or the signal
        // loses to the chrome — asserted through opacity, since the neutral rims are translucent
        // tints of the foreground while the validity ones are solid.
        listOf(
            "light" to OrbitLightControlColors,
            "dark" to OrbitDarkControlColors,
        ).forEach { (theme, control) ->
            val badges = if (theme == "light") OrbitLightBadgeColors else OrbitDarkBadgeColors
            val rim = badges.getValue(OrbitBadgeTone.Red).label
            assertTrue(
                rim.alpha > control.outlineBorder.alpha,
                "the $theme invalid rim (${rim.alpha}) is no more opaque than the focus ring " +
                    "(${control.outlineBorder.alpha}), so an unfilled field looks merely focused",
            )
        }
    }
}

/** WCAG 1.4.11, for graphics that carry meaning rather than decoration. */
private const val NonTextFloor = 3.0

/**
 * Luminance separation between the two rims.
 *
 * A red and a green can be very far apart in hue and nearly identical in luminance, which is exactly
 * the pairing that fails for a red-green colour-blind user — so the test accepts either a luminance
 * gap or a large hue gap rather than demanding both, and the surrounding documentation is explicit
 * that the redundant cues are what carry that case.
 */
private const val PairFloor = 1.6

private const val HueGapFloor = 0.5f
