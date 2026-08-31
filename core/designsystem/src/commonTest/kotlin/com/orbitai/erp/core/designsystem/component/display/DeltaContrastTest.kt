package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.ui.graphics.Color
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitDarkBadgeColors
import com.orbitai.erp.core.designsystem.theme.OrbitDarkControlColors
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitLightBadgeColors
import com.orbitai.erp.core.designsystem.theme.OrbitLightControlColors
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrast verification for the filled delta chip.
 *
 * The badge palette was tuned against the most elevated surface in each theme — `#E8E8E8` on light,
 * `#2A2A2A` on dark — because that is the least favourable background a translucent pill can land
 * on. The delta chip breaks that assumption: it sits on a card, which is white on light and
 * near-black on dark, both further from the tuning point than anything the generator considered.
 *
 * On light that is harmless and then some, since a whiter background only helps dark text. On dark
 * it cuts the other way — the card is darker than `#2A2A2A`, so the chip's fill composites darker
 * and its pale label has *more* room, but the fill itself has less separation from the card. Rather
 * than reason about which way each case falls, this re-derives the whole stack over the real
 * backgrounds and checks it.
 */
class DeltaContrastTest {

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
     * Every background the label actually sits on, sampled down the chip.
     *
     * Mirrors `Modifier.orbitGlass`: the fill's alpha eases from `alpha * Sheen` at the top to its
     * nominal value at the bottom — clamped at 1, as the modifier clamps it — while the white
     * highlight fades from its peak to nothing over the same span. The worst point is rarely an
     * endpoint, because those two pull in opposite directions on a light theme.
     */
    private fun backgrounds(fill: Color, peak: Float, card: Color): List<Color> =
        (0..SAMPLES).map { step ->
            val t = step.toFloat() / SAMPLES
            val sheened = fill.copy(
                alpha = (fill.alpha * (OrbitGlass.Sheen + (1f - OrbitGlass.Sheen) * t))
                    .coerceAtMost(1f),
            )
            val highlight = Color.White.copy(alpha = peak * (1f - t))
            highlight.over(sheened.over(card))
        }

    private data class Case(
        val name: String,
        val card: Color,
        val page: Color,
        val peak: Float,
        val palette: Map<OrbitBadgeTone, com.orbitai.erp.core.designsystem.theme.OrbitBadgeColors>,
    )

    private val cases = listOf(
        Case(
            name = "light",
            card = OrbitLightControlColors.cardContainer,
            page = Color.White,
            peak = OrbitGlass.BadgeHighlightLight,
            palette = OrbitLightBadgeColors,
        ),
        Case(
            name = "dark",
            card = OrbitDarkControlColors.cardContainer,
            page = Color.Black,
            peak = OrbitGlass.BadgeHighlightDark,
            palette = OrbitDarkBadgeColors,
        ),
    )

    /** The only two tones a delta ever uses. */
    private val tones = listOf(OrbitBadgeTone.Green, OrbitBadgeTone.Red)

    @Test
    fun `the percentage clears the text minimum on a card`() {
        cases.forEach { case ->
            val card = case.card.over(case.page)
            tones.forEach { tone ->
                val colors = case.palette.getValue(tone)
                backgrounds(colors.container, case.peak, card).forEachIndexed { i, background ->
                    val ratio = contrast(colors.label, background)
                    assertTrue(
                        ratio >= TEXT_MINIMUM,
                        "${case.name} $tone delta label is ${format(ratio)}:1 at sample $i, " +
                            "expected >= $TEXT_MINIMUM:1",
                    )
                }
            }
        }
    }

    @Test
    fun `the arrow clears the graphical minimum on a card`() {
        // The glyph is the only thing distinguishing a rise from a fall for someone who cannot
        // separate the two hues, so it has to survive on its own at 3:1 (WCAG 1.4.11).
        cases.forEach { case ->
            val card = case.card.over(case.page)
            tones.forEach { tone ->
                val colors = case.palette.getValue(tone)
                backgrounds(colors.container, case.peak, card).forEachIndexed { i, background ->
                    val ratio = contrast(colors.icon, background)
                    assertTrue(
                        ratio >= GRAPHICAL_MINIMUM,
                        "${case.name} $tone delta arrow is ${format(ratio)}:1 at sample $i, " +
                            "expected >= $GRAPHICAL_MINIMUM:1",
                    )
                }
            }
        }
    }

    @Test
    fun `the chip is visible against the card it sits on`() {
        // A translucent tint on a white card is a weak signal, and the whole point of filling the
        // chip rather than outlining it is that the fill does some work. It does not need 3:1 — the
        // rim and the glyph carry the meaning — but it cannot be indistinguishable from the card,
        // or the fill is costing saturation and buying nothing.
        cases.forEach { case ->
            val card = case.card.over(case.page)
            tones.forEach { tone ->
                val colors = case.palette.getValue(tone)
                val ratio = contrast(colors.container.over(card), card)
                assertTrue(
                    ratio > 1.1,
                    "${case.name} $tone delta fill is ${format(ratio)}:1 against the card — " +
                        "the chip has no visible body",
                )
            }
        }
    }

    @Test
    fun `the two tones are separable without colour`() {
        // Green and red are the classic confusable pair, so if the chip ever loses its arrow this
        // is what is left. They are close in lightness by design — neither state should look
        // heavier — which is exactly why the arrow is not optional. This pins the fact rather than
        // the hope: if someone later makes the tones differ in lightness enough to be readable in
        // greyscale, the arrow could become optional, and this test is where they would find out.
        cases.forEach { case ->
            val green = case.palette.getValue(OrbitBadgeTone.Green).label
            val red = case.palette.getValue(OrbitBadgeTone.Red).label
            val ratio = contrast(green, red)
            assertTrue(
                ratio < GRAPHICAL_MINIMUM,
                "${case.name} green and red labels are ${format(ratio)}:1 apart, which is enough " +
                    "to tell apart in greyscale — the arrow may no longer be load-bearing, so " +
                    "re-check OrbitDelta's documentation before relaxing anything",
            )
        }
    }

    private fun format(value: Double): String {
        val scaled = (value * 1000).toInt()
        return "${scaled / 1000}.${(scaled % 1000).toString().padStart(3, '0')}"
    }

    private companion object {
        const val SAMPLES = 24
        const val TEXT_MINIMUM = 4.5
        const val GRAPHICAL_MINIMUM = 3.0
    }
}
