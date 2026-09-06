package com.orbitai.erp.core.designsystem.component.badge

import androidx.compose.ui.graphics.Color
import com.orbitai.erp.core.designsystem.theme.OrbitDarkControlColors
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitLightControlColors
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrast for [OrbitRoleBadge] glass stack over card surfaces (account + avatar info popovers).
 */
class OrbitRoleBadgeContrastTest {

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
        alpha = 1f,
    )

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

    @Test
    fun `role badge label clears 4_5 on light and dark cards`() {
        cases.forEach { case ->
            val card = case.card.over(case.page)
            val samples = backgrounds(case.container, case.peak, card)
            val worst = samples.minOf { contrast(case.label, it) }
            assertTrue(
                worst >= 4.5,
                "${case.name} role badge label contrast $worst < 4.5:1",
            )
        }
    }

    private data class Case(
        val name: String,
        val card: Color,
        val page: Color,
        val peak: Float,
        val container: Color,
        val label: Color,
    )

    private val cases = listOf(
        Case(
            name = "light",
            card = OrbitLightControlColors.cardContainer,
            page = Color.White,
            peak = OrbitGlass.BadgeHighlightLight,
            container = Color(0xE61C1C1E),
            label = Color(0xFFFFFFFF),
        ),
        Case(
            name = "dark",
            card = OrbitDarkControlColors.cardContainer,
            page = Color.Black,
            peak = OrbitGlass.BadgeHighlightDark,
            container = Color(0xE6F2F2F7),
            label = Color(0xFF1C1C1E),
        ),
    )

    private companion object {
        const val SAMPLES = 20
    }
}
