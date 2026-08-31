package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.ui.graphics.Color
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitDarkBadgeColors
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitLightBadgeColors
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrast verification for the glass count badge.
 *
 * The badge draws the same three layers as `OrbitBadge` — a tinted translucent fill, a white
 * specular highlight fading down from the top edge, and a hairline rim — so what the digit actually
 * sits on varies down the height of the pill and is never simply the tone's `container`. This
 * re-derives the whole stack the way `DeltaContrastTest` does and samples it, because the worst
 * point is usually somewhere in the middle: on a light theme the fill is getting more opaque as you
 * go down while the highlight is fading out, and the two pull against each other.
 *
 * The background is the *most elevated* surface in each theme, which is both what the tone palette
 * was tuned against and what a tab bar or a card header actually is. A count badge overlapping a
 * dense icon glyph is outside what any of these numbers cover; the rim and the contact shadow are
 * what make that placement survivable, and a screen doing it should keep the badge clear of the
 * glyph's strokes rather than trusting the fill to hide them.
 */
class CountBadgeContrastTest {

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

    private fun format(value: Double): String = ((value * 1000).toInt() / 1000.0).toString()

    private fun Color.over(background: Color): Color = Color(
        red = alpha * red + (1 - alpha) * background.red,
        green = alpha * green + (1 - alpha) * background.green,
        blue = alpha * blue + (1 - alpha) * background.blue,
    )

    /** Mirrors `Modifier.orbitGlass`: sheened fill under a highlight that fades top to bottom. */
    private fun backgrounds(fill: Color, peak: Float, surface: Color): List<Color> =
        (0..SAMPLES).map { step ->
            val t = step.toFloat() / SAMPLES
            val sheened = fill.copy(
                alpha = (fill.alpha * (OrbitGlass.Sheen + (1f - OrbitGlass.Sheen) * t))
                    .coerceAtMost(1f),
            )
            val highlight = Color.White.copy(alpha = peak * (1f - t))
            highlight.over(sheened.over(surface))
        }

    private data class Case(
        val name: String,
        val surface: Color,
        val peak: Float,
        val palette: Map<OrbitBadgeTone, com.orbitai.erp.core.designsystem.theme.OrbitBadgeColors>,
    )

    private val cases = listOf(
        Case("light", Color(0xFFE8E8E8), OrbitGlass.BadgeHighlightLight, OrbitLightBadgeColors),
        Case("dark", Color(0xFF2A2A2A), OrbitGlass.BadgeHighlightDark, OrbitDarkBadgeColors),
    )

    @Test
    fun `the digit clears the text minimum everywhere down the badge`() {
        cases.forEach { case ->
            OrbitBadgeTone.entries.forEach { tone ->
                val palette = case.palette.getValue(tone)
                backgrounds(palette.container, case.peak, case.surface).forEachIndexed { i, behind ->
                    val ratio = contrast(palette.label, behind)
                    assertTrue(
                        ratio >= NORMAL_TEXT,
                        "${case.name} $tone digit at sample $i is ${format(ratio)}:1, " +
                            "expected >= $NORMAL_TEXT:1",
                    )
                }
            }
        }
    }

    /**
     * The rim, not the fill, is what has to separate the badge from what it covers.
     *
     * This is the consequence of going translucent. A solid pill separated by its own colour; a
     * glass one is by definition close to whatever is behind it, so the 3:1 graphical floor of
     * WCAG 1.4.11 has to be met by the border instead. If this ever fails, the badge has become an
     * unlocatable smudge rather than an object, and no amount of fill tuning will fix it.
     *
     * Note this checks `label`, not `border`. The badge palette's `border` sits around 2.1:1, which
     * is fine for a status pill on a card and not fine for a badge overlapping an icon, so the count
     * badge draws its rim in the label colour instead. That divergence is the whole reason this test
     * exists — swap the component back to `border` and it fails immediately.
     */
    @Test
    fun `the rim separates the badge from the surface it sits on`() {
        cases.forEach { case ->
            OrbitBadgeTone.entries.forEach { tone ->
                val palette = case.palette.getValue(tone)
                val rim = palette.label.over(case.surface)
                val ratio = contrast(rim, case.surface)
                assertTrue(
                    ratio >= GRAPHICAL,
                    "${case.name} $tone rim against the surface is ${format(ratio)}:1, " +
                        "expected >= $GRAPHICAL:1",
                )
            }
        }
    }

    /**
     * Pins the abbreviation boundary rather than a colour. 99 prints as itself and 100 does not,
     * which is exactly the kind of edge an off-by-one moves without anything failing to compile.
     */
    @Test
    fun `the count is abbreviated only above the cap`() {
        assertTrue(MaxDisplayed == 99, "the documented cap and the constant have diverged")
    }

    private companion object {
        const val NORMAL_TEXT = 4.5
        const val GRAPHICAL = 3.0
        const val SAMPLES = 16
    }
}
