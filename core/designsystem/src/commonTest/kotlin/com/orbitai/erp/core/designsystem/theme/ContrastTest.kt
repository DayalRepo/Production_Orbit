package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * WCAG 2.1 contrast verification for every text and icon token, in both themes on both platforms.
 *
 * This exists because contrast is the one design property that is objectively checkable, and it
 * silently regresses the moment someone nudges a grey. Compositing an opacity by hand — which is how
 * the secondary and tertiary tiers were derived — is exactly where a token lands at 4.49:1 and
 * nobody notices.
 */
class ContrastTest {

    private fun channel(value: Float): Double {
        val c = value.toDouble()
        return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }

    /** WCAG relative luminance. */
    private fun luminance(color: Color): Double =
        0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)

    private fun contrast(a: Color, b: Color): Double {
        val la = luminance(a)
        val lb = luminance(b)
        val lighter = maxOf(la, lb)
        val darker = minOf(la, lb)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private val allSets = listOf(
        "Android light" to AndroidLightContentColors,
        "Android dark" to AndroidDarkContentColors,
        "iOS light" to IosLightContentColors,
        "iOS dark" to IosDarkContentColors,
    )

    @Test
    fun `primary text meets AAA for normal text`() {
        allSets.forEach { (name, c) ->
            val ratio = contrast(c.textPrimary, c.referenceSurface)
            assertTrue(ratio >= 7.0, "$name textPrimary is $ratio:1, expected >= 7:1")
        }
    }

    @Test
    fun `secondary and tertiary text meet AA for normal text`() {
        allSets.forEach { (name, c) ->
            val secondary = contrast(c.textSecondary, c.referenceSurface)
            assertTrue(secondary >= 4.5, "$name textSecondary is $secondary:1, expected >= 4.5:1")

            // 60% opacity would land at 4.49:1 in the light themes. This asserts the 65% choice.
            val tertiary = contrast(c.textTertiary, c.referenceSurface)
            assertTrue(tertiary >= 4.5, "$name textTertiary is $tertiary:1, expected >= 4.5:1")
        }
    }

    @Test
    fun `icons meet the non-text contrast floor`() {
        allSets.forEach { (name, c) ->
            val primary = contrast(c.iconPrimary, c.referenceSurface)
            assertTrue(primary >= 4.5, "$name iconPrimary is $primary:1, expected >= 4.5:1")

            // WCAG 1.4.11: 3:1 for meaningful non-text content. An inactive tab icon still has to
            // be identifiable, so it is held to this floor rather than treated as decorative.
            val inactive = contrast(c.iconInactive, c.referenceSurface)
            assertTrue(inactive >= 3.0, "$name iconInactive is $inactive:1, expected >= 3:1")
        }
    }

    @Test
    fun `text tiers descend in contrast so hierarchy is visible`() {
        allSets.forEach { (name, c) ->
            val primary = contrast(c.textPrimary, c.referenceSurface)
            val secondary = contrast(c.textSecondary, c.referenceSurface)
            val tertiary = contrast(c.textTertiary, c.referenceSurface)
            val disabled = contrast(c.textDisabled, c.referenceSurface)
            assertTrue(
                primary > secondary && secondary > tertiary && tertiary > disabled,
                "$name tiers are not strictly descending: " +
                    "$primary, $secondary, $tertiary, $disabled",
            )
        }
    }

    @Test
    fun `no theme uses pure black on white or pure white on black`() {
        val white = Color(0xFFFFFFFF)
        val black = Color(0xFF000000)
        allSets.forEach { (name, c) ->
            // Pure-on-pure measures a perfect 21:1 and is still wrong: it causes visual vibration
            // in the light theme and halation in the dark one.
            val pureOnPure = (c.textPrimary == black && c.referenceSurface == white) ||
                (c.textPrimary == white && c.referenceSurface == black)
            assertTrue(!pureOnPure, "$name uses a pure black/white pairing")
            assertTrue(c.referenceSurface != black, "$name surface is pure black")
        }
    }
}
