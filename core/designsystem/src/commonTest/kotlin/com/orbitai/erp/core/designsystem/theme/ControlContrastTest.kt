package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Contrast verification for the monochrome controls — `OrbitButton` and `OrbitIconButton`.
 *
 * Three things stack between a control's label and the page, and each one moves the number:
 *
 * 1. The container may be translucent, so the surface underneath shows through. A tonal control on
 *    a white card and the same control on an elevated grey one are not the same background.
 * 2. The fill is drawn as a sheen ramp — `OrbitGlass.Sheen` is above 1, so the fill is *more*
 *    opaque at the top and eases back to its nominal alpha at the bottom.
 * 3. A white highlight sits over that, strongest at the top edge, and hover brightens it further.
 *
 * So the label is not measured against one background but against a range of them, and the worst
 * point is rarely an endpoint: at the top of a light-theme tonal control the sheen deepens the tint
 * while the highlight adds white, and those two pull in opposite directions. This samples down the
 * whole gradient rather than checking the ends, and it checks the hovered state, which is the
 * brighter and therefore binding one for filled controls.
 */
class ControlContrastTest {

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
     * Every background the label actually sits on, sampled down the control's height.
     *
     * Mirrors `Modifier.orbitGlass`: fill alpha eases from `alpha * sheen` at the top to `alpha` at
     * the bottom — clamped at 1, as the modifier clamps it — with the white highlight fading from
     * `peak` to nothing over the same span.
     */
    private fun backgrounds(
        fill: Color,
        surface: Color,
        sheen: Float,
        highlight: Float,
    ): List<Color> = (0..SAMPLES).map { step ->
        val t = step.toFloat() / SAMPLES
        val fillAlpha = (fill.alpha * (sheen + (1f - sheen) * t)).coerceAtMost(1f)
        val litSurface = fill.copy(alpha = fillAlpha).over(surface)
        Color.White.copy(alpha = highlight * (1f - t)).over(litSurface)
    }

    private val lightSurfaces = listOf(
        "surface" to OrbitPalette.LightSurface,
        "container" to OrbitPalette.LightSurfaceContainer,
        "highest" to OrbitPalette.LightSurfaceHighest,
    )
    private val darkSurfaces = listOf(
        "surface" to OrbitPalette.DarkSurface,
        "container" to OrbitPalette.DarkSurfaceContainer,
        "highest" to OrbitPalette.DarkSurfaceHighest,
    )

    private val themes = listOf(
        Triple("light", OrbitLightControlColors, false),
        Triple("dark", OrbitDarkControlColors, true),
    )

    private fun assertAllSamplesPass(
        case: String,
        content: Color,
        fill: Color,
        surface: Color,
        sheen: Float,
        highlight: Float,
    ) {
        backgrounds(fill, surface, sheen, highlight).forEachIndexed { i, background ->
            val ratio = contrast(content, background)
            assertTrue(
                ratio >= MINIMUM,
                "$case is $ratio:1 at sample $i of $SAMPLES, expected >= $MINIMUM:1",
            )
        }
    }

    @Test
    fun `filled control labels meet AA at rest and on hover`() {
        themes.forEach { (theme, control, isDark) ->
            val peak = if (isDark) {
                OrbitGlass.ButtonHighlightDark
            } else {
                OrbitGlass.ButtonHighlightLight
            }
            val surfaces = if (isDark) darkSurfaces else lightSurfaces
            listOf("resting" to peak, "hovered" to (peak * OrbitGlass.ButtonHoverLift))
                .forEach { (state, highlight) ->
                    surfaces.forEach { (where, surface) ->
                        assertAllSamplesPass(
                            case = "$theme filled label, $state, on $where",
                            content = control.onActionContainer,
                            fill = control.actionContainer,
                            surface = surface,
                            // Primary's fill is opaque, so orbitGlass is called with no sheen.
                            sheen = 1f,
                            highlight = highlight.coerceAtMost(1f),
                        )
                    }
                }
        }
    }

    @Test
    fun `tonal control content meets AA on every surface it can land on`() {
        // The one that genuinely needs the surface loop. A tonal container is translucent, so its
        // effective background is whatever it was placed over — and an icon button toolbar sits on
        // app bars, cards and sheets, all at different elevations.
        themes.forEach { (theme, control, isDark) ->
            val peak = if (isDark) {
                OrbitGlass.ButtonHighlightDark
            } else {
                OrbitGlass.ButtonHighlightLight
            }
            val surfaces = if (isDark) darkSurfaces else lightSurfaces
            listOf("resting" to peak, "hovered" to (peak * OrbitGlass.ButtonHoverLift))
                .forEach { (state, highlight) ->
                    surfaces.forEach { (where, surface) ->
                        assertAllSamplesPass(
                            case = "$theme tonal content, $state, on $where",
                            content = control.controlContent,
                            fill = control.controlContainer,
                            surface = surface,
                            sheen = OrbitGlass.Sheen,
                            highlight = highlight.coerceAtMost(1f),
                        )
                    }
                }
        }
    }

    /**
     * Every glyph colour `OrbitIconButtonStyle` can produce, paired with the light and dark shade of
     * each. Adding a style to that enum means adding a row here.
     *
     * The shade differs per tone *and* per theme on purpose — see the enum — so this list mirrors the
     * selectors rather than reading one field off the palette.
     */
    private val glyphShades: List<Triple<String, Color, Color>> = listOf(
        Triple(
            "Accent",
            OrbitLightBadgeColors.getValue(OrbitBadgeTone.Blue).border,
            OrbitDarkBadgeColors.getValue(OrbitBadgeTone.Blue).border,
        ),
        Triple(
            "Positive",
            OrbitLightBadgeColors.getValue(OrbitBadgeTone.Green).solidContainer,
            OrbitDarkBadgeColors.getValue(OrbitBadgeTone.Green).solidContainer,
        ),
        Triple(
            "Destructive",
            OrbitLightBadgeColors.getValue(OrbitBadgeTone.Red).border,
            OrbitDarkBadgeColors.getValue(OrbitBadgeTone.Red).solidContainer,
        ),
    )

    @Test
    fun `uncontained control content meets AA on every surface`() {
        // Outline, Text and Plain draw no fill, so this is the plain text case — but it still has
        // to hold on the brightest surface in the theme, which is not the one the token was picked
        // against.
        themes.forEach { (theme, control, isDark) ->
            (if (isDark) darkSurfaces else lightSurfaces).forEach { (where, surface) ->
                val ratio = contrast(control.controlContent, surface)
                assertTrue(
                    ratio >= MINIMUM,
                    "$theme uncontained content on $where is $ratio:1, expected >= $MINIMUM:1",
                )
            }
        }
    }

    @Test
    fun `every icon glyph shade clears the graphical minimum on its ring`() {
        // 3:1, not 4.5:1, and the distinction is the whole reason these glyphs are allowed to be light
        // blue and light red rather than near-navy and near-maroon. A glyph is a graphical object, so
        // WCAG 1.4.11 governs it; 1.4.3's 4.5:1 governs text. Holding an icon to the text minimum is
        // not caution, it is a category error that costs the component its whole palette.
        //
        // The background checked here is the ring fill composited over each surface, because that is
        // what is actually behind the glyph — testing against the bare surface would flatter the light
        // theme, where a white fill lightens the background and makes the glyph's job harder.
        listOf(
            "light" to (lightSurfaces to OrbitLightControlColors.ringContainer),
            "dark" to (darkSurfaces to OrbitDarkControlColors.ringContainer),
        ).forEach { (theme, pair) ->
            val (surfaces, fill) = pair
            glyphShades.forEach { (style, light, dark) ->
                // `border` carries alpha on a badge, where it is a rim over a tint. The component
                // drops it, and so must this.
                val glyph = (if (theme == "light") light else dark).copy(alpha = 1f)
                surfaces.forEach { (where, surface) ->
                    val behind = fill.over(surface)
                    val ratio = contrast(glyph, behind)
                    assertTrue(
                        ratio >= GRAPHICAL_MINIMUM,
                        "the $theme $style glyph on a ring over $where is $ratio:1, " +
                            "expected >= $GRAPHICAL_MINIMUM:1",
                    )
                }
            }
        }
    }

    @Test
    fun `the icon ring is achromatic in both themes`() {
        // White on light and grey on dark. The glyph is carrying semantic colour, and a tinted ring
        // behind it would both compete with that and eat the contrast this file spends its time
        // protecting. This is cheap to assert and easy to break by "warming up" the fill.
        listOf(
            "light" to OrbitLightControlColors.ringContainer,
            "dark" to OrbitDarkControlColors.ringContainer,
        ).forEach { (theme, fill) ->
            val spread = maxOf(fill.red, fill.green, fill.blue) -
                minOf(fill.red, fill.green, fill.blue)
            assertTrue(
                spread <= 0.03f,
                "the $theme icon ring fill has a channel spread of $spread, which is a hue",
            )
        }
    }

    @Test
    fun `an inactive unfilled control meets AA on a faded label`() {
        // Inactive is dimmed but still tappable, so it is live UI and owes the full 4.5:1 for text,
        // not the 3:1 non-text floor. This is the assertion that fixes OrbitAlpha.Inactive at 0.65:
        // the conventional 0.60 puts the light-theme label at 4.47:1 on white, which fails.
        themes.forEach { (theme, control, isDark) ->
            (if (isDark) darkSurfaces else lightSurfaces).forEach { (where, surface) ->
                val faded = control.controlContent
                    .copy(alpha = OrbitAlpha.Inactive)
                    .over(surface)
                val ratio = contrast(faded, surface)
                assertTrue(
                    ratio >= MINIMUM,
                    "$theme inactive label on $where is $ratio:1, expected >= $MINIMUM:1",
                )
            }
        }
    }

    @Test
    fun `an inverted fill cannot survive being faded`() {
        // Now only reachable through a selected `OrbitChip` — buttons became tinted chips and icon
        // buttons lost their containers entirely — but kept, because the constraint is a property of
        // the palette rather than of any one component, and the next control to reach for an inverted
        // fill will hit it again.
        //
        // Records the two dead ends that shaped the inactive state, so nobody re-walks them.
        //
        // Both start from the same place: an inverted fill carries a near-white label, and anything
        // that lightens the fill closes the gap. Fading the fill alone reaches about 3.8:1 at the
        // top of the highlight; fading fill and label together reaches about 2.6:1. Inactive is
        // still tappable and owes 4.5:1, so `OrbitButton` and `OrbitIconButton` step an inactive
        // inverted control down to the tonal treatment — verified by the tonal test above — rather
        // than fading it at all.
        val control = OrbitLightControlColors
        val surface = OrbitPalette.LightSurface
        val fadedFill = control.actionContainer.copy(alpha = OrbitAlpha.Inactive).over(surface)
        val lit = Color.White
            .copy(
                alpha = OrbitGlass.ButtonHighlightLight * OrbitGlass.ButtonHoverLift *
                    OrbitAlpha.Inactive,
            )
            .over(fadedFill)

        assertTrue(
            contrast(control.onActionContainer, lit) < MINIMUM,
            "a faded inverted fill now clears $MINIMUM:1, so the demotion in OrbitButton may be " +
                "unnecessary — re-derive it before removing either",
        )
        assertTrue(
            contrast(control.onActionContainer.copy(alpha = OrbitAlpha.Inactive).over(lit), lit) <
                MINIMUM,
            "fading fill and label together now clears $MINIMUM:1",
        )
    }

    @Test
    fun `the action container inverts against its theme`() {
        // The property the whole monochrome scheme rests on. If someone lightens the light-theme
        // action container to soften it, the button stops being the loudest thing on the screen and
        // every other assertion here still passes.
        val light = luminance(OrbitLightControlColors.actionContainer)
        val dark = luminance(OrbitDarkControlColors.actionContainer)
        assertTrue(light < 0.1, "the light-theme action container is not dark (luminance $light)")
        assertTrue(dark > 0.6, "the dark-theme action container is not light (luminance $dark)")
    }

    @Test
    fun `neither extreme of the monochrome scale is pure`() {
        // Pure white on pure black is what the house rules forbid, and a filled button is the most
        // likely place for it to creep back in.
        listOf(
            "light container" to OrbitLightControlColors.actionContainer,
            "light content" to OrbitLightControlColors.onActionContainer,
            "dark container" to OrbitDarkControlColors.actionContainer,
            "dark content" to OrbitDarkControlColors.onActionContainer,
        ).forEach { (name, color) ->
            assertTrue(color != Color.Black && color != Color.White, "$name is pure")
        }
    }

    @Test
    fun `the outline ring is stronger than the tonal rim`() {
        // On an Outline control the ring is the only thing bounding the target; on a tonal one the
        // fill already does that, so its rim is free to be a highlight rather than a border.
        themes.forEach { (theme, control, _) ->
            assertTrue(
                control.outlineBorder.alpha > control.controlBorder.alpha,
                "$theme outline ring is not stronger than its tonal rim",
            )
        }
    }

    @Test
    fun `the button highlight never exceeds the badge highlight`() {
        // A translucent badge fill can absorb a strong highlight because it competes with whatever
        // shows through. A filled button's cannot, so it gets the same or less.
        //
        // In the light theme it is strictly less — 0.14 against 0.22 — because using the badge
        // figure there pushed a label under the minimum. In the dark theme both sit at 0.10, which
        // is already as low as the highlight can go and still read as light on the top edge, so
        // this is an upper bound rather than a strict inequality.
        assertTrue(
            OrbitGlass.ButtonHighlightLight < OrbitGlass.BadgeHighlightLight,
            "the light button highlight is no weaker than the badge one",
        )
        assertTrue(
            OrbitGlass.ButtonHighlightDark <= OrbitGlass.BadgeHighlightDark,
            "the dark button highlight is stronger than the badge one",
        )
    }

    private companion object {
        const val SAMPLES = 10
        const val MINIMUM = 4.5

        /**
         * The non-text minimum, WCAG 1.4.11. Applies to glyphs, rims and any other graphical object
         * that has to be distinguishable to be usable.
         */
        const val GRAPHICAL_MINIMUM = 3.0
    }
}
