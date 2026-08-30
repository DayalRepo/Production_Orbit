package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Contrast verification for the badge palette.
 *
 * `tools/gen_designsystem.py` already tunes every shade until it passes, so this looks redundant.
 * It is not: the generator proves the numbers were right when they were produced, and this proves
 * they are still right in the file. Someone hand-editing one hex to "warm up the amber a bit" is
 * the realistic failure, and it would sail past code review.
 *
 * The fill is translucent, so a badge's real background is the whole glass stack composited over
 * whatever is behind it. Everything here is measured against the *most elevated* surface in each
 * theme, which is the least favourable case a badge can land on — a lower surface only widens the
 * margin — and sampled down the pill's height rather than at its edges, because the sheen and the
 * white highlight brighten it from opposite directions and the worst point can be in the middle.
 */
class BadgeContrastTest {

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

    /** Source-over composite of a translucent [this] onto an opaque [background]. */
    private fun Color.compositeOver(background: Color): Color = Color(
        red = alpha * red + (1 - alpha) * background.red,
        green = alpha * green + (1 - alpha) * background.green,
        blue = alpha * blue + (1 - alpha) * background.blue,
    )

    private data class Theme(
        val name: String,
        val palette: Map<OrbitBadgeTone, OrbitBadgeColors>,
        val surface: Color,
        val highlight: Float,
    )

    /**
     * Four cases, not two, because these tones are no longer worn only by badges.
     *
     * `OrbitButton`'s tinted variants draw the same containers and labels, and a hovered button
     * lifts its highlight by `ButtonHoverLift` — brighter than any badge ever gets. That is the
     * binding case in the light theme, where a dark label sits on a background the highlight is
     * washing out, so it has to be measured rather than assumed to be covered by the resting one.
     * `tools/gen_designsystem.py` tunes against both levels for exactly this reason.
     */
    private val themes = listOf(
        Theme(
            "light",
            OrbitLightBadgeColors,
            OrbitPalette.LightSurfaceHighest,
            OrbitGlass.BadgeHighlightLight,
        ),
        Theme(
            "dark",
            OrbitDarkBadgeColors,
            OrbitPalette.DarkSurfaceHighest,
            OrbitGlass.BadgeHighlightDark,
        ),
        Theme(
            "light hovered",
            OrbitLightBadgeColors,
            OrbitPalette.LightSurfaceHighest,
            (OrbitGlass.BadgeHighlightLight * OrbitGlass.ButtonHoverLift).coerceAtMost(1f),
        ),
        Theme(
            "dark hovered",
            OrbitDarkBadgeColors,
            OrbitPalette.DarkSurfaceHighest,
            (OrbitGlass.BadgeHighlightDark * OrbitGlass.ButtonHoverLift).coerceAtMost(1f),
        ),
    )

    /**
     * Every background the glass stack presents, sampled down the pill.
     *
     * This mirrors `glass_backgrounds` in `tools/gen_designsystem.py` and must keep mirroring
     * `Modifier.orbitGlass`. Both gradients are linear two-stop gradients there, which is what
     * makes linear interpolation the right model here.
     */
    private fun Theme.glassBackgrounds(container: Color): List<Color> {
        val topAlpha = (container.alpha * OrbitGlass.Sheen).coerceAtMost(1f)
        return (0 until Samples).map { i ->
            val t = i.toFloat() / (Samples - 1)
            val tinted = container
                .copy(alpha = topAlpha + (container.alpha - topAlpha) * t)
                .compositeOver(surface)
            Color.White.copy(alpha = highlight * (1 - t)).compositeOver(tinted)
        }
    }

    /** Contrast against the least favourable point of the gradient. */
    private fun Theme.worstContrast(color: Color, container: Color): Double =
        glassBackgrounds(container).minOf { contrast(color, it) }

    @Test
    fun `every tone is defined in both themes`() {
        themes.forEach { (name, palette) ->
            assertEquals(
                OrbitBadgeTone.entries.size,
                palette.size,
                "$name badge palette covers ${palette.size} of ${OrbitBadgeTone.entries.size} tones",
            )
            OrbitBadgeTone.entries.forEach { tone ->
                assertTrue(tone in palette, "$name palette is missing $tone")
            }
        }
    }

    @Test
    fun `badge labels meet AA for normal text on the glassed container`() {
        themes.forEach { theme ->
            theme.palette.forEach { (tone, colors) ->
                val ratio = theme.worstContrast(colors.label, colors.container)
                assertTrue(ratio >= 4.5, "${theme.name} $tone label is $ratio:1, expected >= 4.5:1")
            }
        }
    }

    @Test
    fun `badge icons meet AA for normal text on the glassed container`() {
        // 3:1 is the floor WCAG 1.4.11 sets for non-text content, but a badge glyph carries the
        // status as much as the word does, so it is held to the text minimum.
        themes.forEach { theme ->
            theme.palette.forEach { (tone, colors) ->
                val ratio = theme.worstContrast(colors.icon, colors.container)
                assertTrue(ratio >= 4.5, "${theme.name} $tone icon is $ratio:1, expected >= 4.5:1")
            }
        }
    }

    @Test
    fun `icon is a distinct shade from the label and never lower contrast`() {
        themes.forEach { theme ->
            theme.palette.forEach { (tone, colors) ->
                assertNotEquals(
                    colors.label,
                    colors.icon,
                    "${theme.name} $tone uses one colour for label and icon; they are meant to differ",
                )
                assertTrue(
                    theme.worstContrast(colors.icon, colors.container) >=
                        theme.worstContrast(colors.label, colors.container),
                    "${theme.name} $tone icon has less contrast than its label",
                )
            }
        }
    }

    @Test
    fun `solid emphasis pairs meet AA for normal text`() {
        themes.forEach { (name, palette) ->
            palette.forEach { (tone, colors) ->
                val ratio = contrast(colors.onSolidContainer, colors.solidContainer)
                assertTrue(ratio >= 4.5, "$name $tone solid pair is $ratio:1, expected >= 4.5:1")
            }
        }
    }

    @Test
    fun `container and border are translucent so the glass effect actually reads`() {
        themes.forEach { (name, palette) ->
            palette.forEach { (tone, colors) ->
                assertTrue(
                    colors.container.alpha < 1f,
                    "$name $tone container is opaque; nothing would tint through it",
                )
                assertTrue(
                    colors.border.alpha < 1f,
                    "$name $tone border is opaque",
                )
                assertTrue(
                    colors.border.alpha > colors.container.alpha,
                    "$name $tone border is no stronger than its fill, so the pill has no edge",
                )
            }
        }
    }

    @Test
    fun `solid containers are opaque`() {
        themes.forEach { (name, palette) ->
            palette.forEach { (tone, colors) ->
                assertEquals(
                    1f,
                    colors.solidContainer.alpha,
                    "$name $tone solidContainer must be opaque; its contrast pair assumes it",
                )
            }
        }
    }

    private companion object {
        /** Matches `GLASS_SAMPLES` in the generator. */
        const val Samples = 11
    }
}
