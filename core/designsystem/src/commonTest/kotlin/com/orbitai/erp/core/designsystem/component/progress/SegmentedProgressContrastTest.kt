package com.orbitai.erp.core.designsystem.component.progress

import androidx.compose.ui.graphics.Color
import com.orbitai.erp.core.designsystem.theme.OrbitDarkControlColors
import com.orbitai.erp.core.designsystem.theme.OrbitLightControlColors
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrast verification for the segmented progress track.
 *
 * The reading this component exists to deliver is the boundary between the lit run and the unlit
 * one, which makes that boundary a graphical object conveying meaning and puts it under WCAG 1.4.11
 * at 3:1. It is easy to lose: the track is translucent, so it takes the colour of the card beneath
 * it, and both the fill and the card are theme-dependent. Nothing about the component surfaces that
 * — a bar with a 1.6:1 boundary looks perfectly reasonable to a designer who already knows the
 * value, and is a flat rectangle to somebody who does not.
 *
 * The pairing also runs in opposite directions per theme — a darker blue on light, a lighter blue on
 * dark — so a change that helps one theme routinely hurts the other. That is exactly the sort of
 * thing that needs a test rather than a review.
 */
class SegmentedProgressContrastTest {

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

    /**
     * A lit slat at its lightest, which is the top edge where the specular highlight peaks.
     *
     * The highlight only ever moves the fill toward white, so on the light theme — where the fill is
     * the darker of the two colours — the top edge is the worst case and the bottom is comfortably
     * better. On dark the fill is the lighter colour and the highlight helps, so the *bottom* binds
     * there. Both ends are checked below rather than reasoning about which is which per theme.
     */
    private fun litTop(fill: Color, highlight: Float, card: Color): Color =
        Color.White.copy(alpha = highlight).over(fill.over(card))

    private data class Case(
        val name: String,
        val fill: Color,
        val track: Color,
        val card: Color,
        val highlight: Float,
    )

    private val cases = listOf(
        Case(
            name = "light",
            fill = OrbitPalette.Blue50,
            track = OrbitLightControlColors.controlContainer,
            card = OrbitLightControlColors.cardContainer,
            highlight = OrbitProgressDefaults.SlatHighlightLight,
        ),
        Case(
            name = "dark",
            fill = OrbitPalette.Blue80,
            track = OrbitDarkControlColors.controlContainer,
            card = OrbitDarkControlColors.cardContainer,
            highlight = OrbitProgressDefaults.SlatHighlightDark,
        ),
    )

    @Test
    fun `the lit run is distinguishable from the unlit run`() {
        cases.forEach { case ->
            // The card itself is near-opaque but not fully, so it composites over the page first.
            // Using the page colour that makes the card lightest on light and darkest on dark keeps
            // this pinned to the real worst case rather than an average one.
            val page = if (case.name == "light") Color.White else Color.Black
            val card = case.card.over(page)

            val track = case.track.over(card)
            val bottom = case.fill.over(card)
            val top = litTop(case.fill, case.highlight, card)

            listOf("bottom" to bottom, "top" to top).forEach { (edge, lit) ->
                val ratio = contrast(lit, track)
                assertTrue(
                    ratio >= GRAPHICAL_MINIMUM,
                    "${case.name} lit slat at its $edge is ${format(ratio)}:1 against the track, " +
                        "expected >= $GRAPHICAL_MINIMUM:1 (WCAG 1.4.11)",
                )
            }
        }
    }

    @Test
    fun `an unlit slat is still visible against the card`() {
        // The unlit run has to read as a recess rather than as nothing at all: a bar whose empty
        // part is invisible has no length, so there is nothing for the lit part to be a fraction
        // *of*, and 30% and 90% start to look the same. This is well under 3:1 by design — it is a
        // boundary between two decorative surfaces, not one that carries the reading — but it
        // cannot be 1:1.
        cases.forEach { case ->
            val page = if (case.name == "light") Color.White else Color.Black
            val card = case.card.over(page)
            val ratio = contrast(case.track.over(card), card)

            assertTrue(
                ratio > 1.05,
                "${case.name} unlit slat is ${format(ratio)}:1 against the card — the empty part of " +
                    "the bar has disappeared, so the bar has no visible length",
            )
        }
    }

    @Test
    fun `the fill runs opposite ways in the two themes`() {
        // Encodes the reason there are two blues rather than one. If somebody ever "simplifies" this
        // to a single token, the light theme's fill goes light on a white card or the dark theme's
        // goes dark on a black one, and the test above starts failing for a reason that is not
        // obvious from the diff. This one names it.
        val light = cases.first { it.name == "light" }
        val dark = cases.first { it.name == "dark" }

        assertTrue(
            luminance(light.fill) < luminance(light.track.over(light.card.over(Color.White))),
            "the light theme's fill must be darker than its track",
        )
        assertTrue(
            luminance(dark.fill) > luminance(dark.track.over(dark.card.over(Color.Black))),
            "the dark theme's fill must be lighter than its track",
        )
    }

    private fun format(value: Double): String {
        val scaled = (value * 1000).toInt()
        return "${scaled / 1000}.${(scaled % 1000).toString().padStart(3, '0')}"
    }

    private companion object {
        /** WCAG 1.4.11, non-text contrast. */
        const val GRAPHICAL_MINIMUM = 3.0
    }
}
