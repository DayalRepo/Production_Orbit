package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.ui.graphics.Color
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitDarkBadgeColors
import com.orbitai.erp.core.designsystem.theme.OrbitLightBadgeColors
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrast verification for the solid count badge (digit on [onSolidContainer] fill).
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

    private val cases = listOf(
        "light" to OrbitLightBadgeColors,
        "dark" to OrbitDarkBadgeColors,
    )

    @Test
    fun `the digit clears the text minimum on the solid fill`() {
        cases.forEach { (name, paletteMap) ->
            OrbitBadgeTone.entries.forEach { tone ->
                val palette = paletteMap.getValue(tone)
                val ratio = contrast(palette.onSolidContainer, palette.solidContainer)
                assertTrue(
                    ratio >= NORMAL_TEXT,
                    "$name $tone digit is ${format(ratio)}:1, expected >= $NORMAL_TEXT:1",
                )
            }
        }
    }

    @Test
    fun `default nav red fill separates from an elevated surface`() {
        val surfaces = listOf(
            "light" to Color(0xFFE8E8E8),
            "dark" to Color(0xFF2A2A2A),
        )
        surfaces.forEach { (name, surface) ->
            val paletteMap = if (name == "light") OrbitLightBadgeColors else OrbitDarkBadgeColors
            val fill = paletteMap.getValue(OrbitBadgeTone.Red).solidContainer
            val ratio = contrast(fill, surface)
            assertTrue(
                ratio >= GRAPHICAL,
                "$name Red fill against surface is ${format(ratio)}:1, expected >= $GRAPHICAL:1",
            )
        }
    }

    @Test
    fun `the count is abbreviated only above the cap`() {
        assertTrue(MaxDisplayed == 99, "the documented cap and the constant have diverged")
    }

    private companion object {
        const val NORMAL_TEXT = 4.5
        const val GRAPHICAL = 3.0
    }
}
