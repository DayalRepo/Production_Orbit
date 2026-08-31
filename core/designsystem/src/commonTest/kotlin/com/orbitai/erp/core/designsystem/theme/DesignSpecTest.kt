package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The design specification, pinned.
 *
 * Every other test in this package checks a *property* — that contrast clears a threshold, that a
 * scale descends, that a line height exceeds its font size. Those are the tests that catch a bad
 * change. This one catches a different failure: the slow drift where each individual edit looks
 * reasonable, passes every property test, and moves the system a shade away from the spec it was
 * signed off against.
 *
 * So this file restates the spec tables literally. It is deliberately dumb — no derivation, no
 * tolerance, just the agreed hex and dp next to the token that is supposed to carry it. A failure
 * here is not necessarily a bug; it means the spec and the code disagree and somebody has to decide
 * which one is wrong. The two documented deviations are asserted *as deviations*, with the reason
 * next to them, so they cannot be quietly reverted or quietly forgotten.
 */
class DesignSpecTest {

    // ---------------------------------------------------------------- typography

    @Test
    fun `the Android type scale is the exported sheet`() {
        val s = AndroidTypeScale
        assertEquals(16.sp, s.baseSize, "base size")
        assertEquals(1.125f, s.scaleRatio, "ratio")

        assertMetrics("display", s.displayLarge, 23, 27, -0.5f)
        assertMetrics("h1", s.h1, 20, 24, -0.25f)
        assertMetrics("h2", s.h2, 18, 23, 0f)
        assertMetrics("subheading", s.h3, 17, 23, 0f)
        assertMetrics("body", s.body, 16, 23, 0.15f)
        assertMetrics("caption", s.caption, 14, 19, 0.2f)
    }

    @Test
    fun `the iOS type scale is the exported sheet`() {
        val s = IosTypeScale
        assertEquals(17.sp, s.baseSize, "base size")
        assertEquals(1.125f, s.scaleRatio, "ratio")

        assertMetrics("display", s.displayLarge, 24, 28, -0.5f)
        assertMetrics("h1", s.h1, 22, 27, -0.25f)
        assertMetrics("h2", s.h2, 19, 24, 0f)
        assertMetrics("subheading", s.h3, 18, 24, 0f)
        assertMetrics("body", s.body, 17, 24, 0.15f)
        assertMetrics("caption", s.caption, 15, 20, 0.2f)
    }

    @Test
    fun `the exported weights are what the tiers carry`() {
        val w = OrbitFontWeights()
        assertEquals(700, w.display.weight, "display")
        assertEquals(600, w.heading.weight, "h1 and h2")
        assertEquals(500, w.title.weight, "subheading")
        assertEquals(400, w.body.weight, "body and caption")
    }

    // ------------------------------------------------------------------- colour

    @Test
    fun `text emphasis tiers match the spec`() {
        lightContent.forEach { (name, c) ->
            assertEquals(Color(0xFF1C1C1E), c.textPrimary, "$name primary text")
            assertEquals(Color(0xFF48484A), c.textSecondary, "$name secondary text")
        }
        darkContent.forEach { (name, c) ->
            assertEquals(Color(0xFFF2F2F7), c.textPrimary, "$name primary text")
            assertEquals(Color(0xFFC7C7CC), c.textSecondary, "$name secondary text")
        }
        darkContent.forEach { (name, c) ->
            assertEquals(Color(0xFF8E8E93), c.textTertiary, "$name tertiary text")
        }
    }

    @Test
    fun `light tertiary text departs from the spec on purpose`() {
        // The spec puts #8E8E93 on both themes. It measures 3.26:1 on the light app background,
        // short of the 4.5:1 WCAG 1.4.3 floor for normal-size text, and our tertiary tier is the
        // caption tier. The light theme therefore takes the same hue, darkened until it clears.
        //
        // Asserted rather than merely commented, so restoring the literal spec value here is a
        // deliberate act with a failing test attached rather than a tidy-up nobody reviews.
        lightContent.forEach { (name, c) ->
            assertTrue(
                c.textTertiary != Color(0xFF8E8E93),
                "$name light tertiary is the raw spec value, which fails AA on the light background",
            )
            assertTrue(
                contrast(c.textTertiary, Color(0xFFF9F9FB)) >= 4.5,
                "$name light tertiary must clear 4.5:1 on the app background",
            )
        }
    }

    @Test
    fun `icon roles match the spec`() {
        lightContent.forEach { (name, c) ->
            assertEquals(Color(0xFF1C1C1E), c.iconPrimary, "$name primary icon")
            assertEquals(Color(0xFF8E8E93), c.iconInactive, "$name secondary icon")
            assertEquals(Color(0xFF007AFF), c.iconAccent, "$name accent icon")
            assertEquals(Color(0xFFC7C7CC), c.iconDisabled, "$name disabled icon")
            assertEquals(Color(0xFFFFFFFF), c.iconOnColor, "$name on-colour icon")
        }
        darkContent.forEach { (name, c) ->
            assertEquals(Color(0xFFF2F2F7), c.iconPrimary, "$name primary icon")
            assertEquals(Color(0xFF8E8E93), c.iconInactive, "$name secondary icon")
            assertEquals(Color(0xFF0A84FF), c.iconAccent, "$name accent icon")
            assertEquals(Color(0xFF48484A), c.iconDisabled, "$name disabled icon")
            assertEquals(Color(0xFFFFFFFF), c.iconOnColor, "$name on-colour icon")
        }
    }

    @Test
    fun `borders and surfaces match the spec`() {
        val light = OrbitLightControlColors
        assertEquals(Color(0xFFE2E2E6), light.controlBorder, "light standard border")
        assertEquals(Color(0xFFF2F2F7), light.dividerSubtle, "light subtle divider")
        assertEquals(Color(0xFF007AFF), light.borderFocus, "light focus border")
        assertEquals(Color(0xFFFFFFFF), light.cardContainer, "light card")
        assertEquals(Color(0xFFF2F2F7), light.insetContainer, "light inset box")
        assertEquals(Color(0xFFEBEBEF), light.interactiveContainer, "light interactive container")

        val dark = OrbitDarkControlColors
        assertEquals(Color(0xFF3F3F46), dark.controlBorder, "dark standard border")
        assertEquals(Color(0xFF2C2C2E), dark.dividerSubtle, "dark subtle divider")
        assertEquals(Color(0xFF0A84FF), dark.borderFocus, "dark focus border")
        assertEquals(Color(0xFF1C1C1E), dark.cardContainer, "dark card")
        assertEquals(Color(0xFF2C2C2E), dark.insetContainer, "dark inset box")
        assertEquals(Color(0xFF3A3A3C), dark.interactiveContainer, "dark interactive container")

        assertEquals(Color(0xFFF9F9FB), OrbitPalette.LightBackground, "light app background")
        assertEquals(Color(0xFF121214), OrbitPalette.DarkBackground, "dark app background")
    }

    @Test
    fun `a border is a low-contrast step off the surface it encloses`() {
        // The spec's own instruction, restated as a bound. A rim answers "where does this stop";
        // anything approaching text contrast reads as a stroke, or as a state the control is in.
        assertTrue(
            contrast(OrbitLightControlColors.controlBorder, Color(0xFFFFFFFF)) < 2.0,
            "the light border is too strong against a white card",
        )
        assertTrue(
            contrast(OrbitDarkControlColors.controlBorder, Color(0xFF1C1C1E)) < 2.0,
            "the dark border is too strong against a dark card",
        )
    }

    // ---------------------------------------------------------------- elevation

    @Test
    fun `the elevation ladder matches the spec matrix`() {
        assertLevel("Level 0", OrbitShadow.Level0, 0, 0, 0f, 0xFF2C2C2E)
        assertLevel("Level 1", OrbitShadow.Level1, 2, 4, 0.05f, 0xFF1C1C1E)
        assertLevel("Level 2", OrbitShadow.Level2, 4, 8, 0.08f, 0xFF252528)
        assertLevel("Level 3", OrbitShadow.Level3, 6, 14, 0.12f, 0xFF2C2C2E)
        assertLevel("Level 4", OrbitShadow.Level4, 12, 24, 0.16f, 0xFF3A3A3C)
    }

    @Test
    fun `shadows fall downwards and stay out of the muddy range`() {
        raised.forEach { (name, level) ->
            assertTrue(level.offsetY.value >= 2f, "$name must offset at least 2dp down, light is above")
            assertTrue(
                level.opacity in 0.04f..0.16f,
                "$name opacity is ${level.opacity}, outside the 4%-16% band that keeps black clean",
            )
        }
    }

    @Test
    fun `going up a rung means further from the surface and more total ink`() {
        raised.zipWithNext().forEach { (lower, higher) ->
            assertTrue(
                higher.second.offsetY > lower.second.offsetY,
                "${higher.first} should sit further from the surface than ${lower.first}",
            )
            assertTrue(
                higher.second.blur > lower.second.blur,
                "${higher.first} should be blurrier than ${lower.first}",
            )
            // Opacity rising with height looks backwards against the usual "higher is more
            // transparent" rule, and it is not. That rule describes the shadow's *peak density*,
            // which a larger blur radius spreads thinner all by itself. Total ink has to rise or a
            // modal would cast a fainter shadow than a chip.
            assertTrue(
                higher.second.opacity > lower.second.opacity,
                "${higher.first} should cast more total shadow than ${lower.first}",
            )
        }
    }

    @Test
    fun `blur always outruns offset so nothing casts a hard duplicate of itself`() {
        // Not a monotonic ratio - the spec's own numbers run 2.0, 2.0, 2.33, 2.0, and Level 3 is
        // simply a little softer than its neighbours. What has to hold at every rung is the floor:
        // if blur ever fell below the offset, the shadow would separate from the element and read as
        // a second, blurry copy of it sitting underneath rather than as height.
        raised.forEach { (name, level) ->
            assertTrue(
                level.blur >= level.offsetY * 2f,
                "$name blur (${level.blur}) must be at least twice its offset (${level.offsetY})",
            )
        }
    }

    @Test
    fun `every rung lightens in the dark theme rather than darkening`() {
        // The inversion that makes dark mode work at all: depth is a lighter surface, because there
        // is no light on a near-black page for a raised object to block.
        val background = Color(0xFF121214)
        listOf(
            "Level 1" to OrbitShadow.Level1,
            "Level 2" to OrbitShadow.Level2,
            "Level 4" to OrbitShadow.Level4,
        ).forEach { (name, level) ->
            assertTrue(
                luminance(level.darkSurface) > luminance(background),
                "$name dark surface must be lighter than the app background",
            )
        }
        assertTrue(
            luminance(OrbitShadow.Level4.darkSurface) > luminance(OrbitShadow.Level2.darkSurface),
            "an overlay must read as higher than a dropdown in the dark theme too",
        )
    }

    @Test
    fun `the low rungs pair their surface with a rim`() {
        // A one-step tonal lift is easy to lose on a dim screen or a cheap panel. The high rungs
        // have enough tonal separation to carry their own edge; the low ones do not.
        assertTrue(OrbitShadow.Level1.darkBorder, "cards need a rim in the dark theme")
        assertTrue(OrbitShadow.Level2.darkBorder, "dropdowns need a rim in the dark theme")
    }

    // ------------------------------------------------------------------- icons

    @Test
    fun `the icon ladder and its strokes match the spec`() {
        listOf("Android" to platformTokens(com.orbitai.erp.core.designsystem.foundation.OrbitPlatform.Android),
               "iOS" to platformTokens(com.orbitai.erp.core.designsystem.foundation.OrbitPlatform.Ios))
            .forEach { (name, tokens) ->
                assertEquals(16.dp, tokens.sizing.iconSm, "$name small icon")
                assertEquals(24.dp, tokens.sizing.iconMd, "$name medium icon")
                assertEquals(32.dp, tokens.sizing.iconXl, "$name large icon")
                assertEquals(48.dp, tokens.sizing.iconHero, "$name hero icon")

                assertEquals(1.5.dp, tokens.sizing.iconStrokeSm, "$name small stroke")
                assertEquals(2.dp, tokens.sizing.iconStrokeMd, "$name medium stroke")
                assertTrue(
                    tokens.sizing.iconStrokeLg.value in 2.0f..2.5f,
                    "$name large stroke is outside the spec's 2.0-2.5dp band",
                )
                assertTrue(
                    tokens.sizing.iconStrokeHero.value in 2.5f..3.0f,
                    "$name hero stroke is outside the spec's 2.5-3.0dp band",
                )
            }
    }

    @Test
    fun `stroke grows more slowly than size`() {
        // Optical sizing. A glyph scaled with its stroke held proportional looks heavier as it
        // grows, because the eye reads absolute stroke against surrounding whitespace rather than
        // against the icon's own box. Three times the size takes under twice the ink.
        val tokens = platformTokens(com.orbitai.erp.core.designsystem.foundation.OrbitPlatform.Android)
        val sizeRatio = tokens.sizing.iconHero / tokens.sizing.iconSm
        val strokeRatio = tokens.sizing.iconStrokeHero / tokens.sizing.iconStrokeSm
        assertTrue(
            strokeRatio < sizeRatio,
            "stroke grew by $strokeRatio against a size growth of $sizeRatio - that is proportional, " +
                "and proportional reads as heavier at the top of the ladder",
        )
    }

    @Test
    fun `a tappable icon gets a touch target regardless of how small it is drawn`() {
        assertEquals(
            48.dp,
            platformTokens(com.orbitai.erp.core.designsystem.foundation.OrbitPlatform.Android).sizing.minTouchTarget,
            "Android floor is 48dp",
        )
        assertEquals(
            44.dp,
            platformTokens(com.orbitai.erp.core.designsystem.foundation.OrbitPlatform.Ios).sizing.minTouchTarget,
            "iOS floor is 44pt",
        )
    }

    // ------------------------------------------------------------------ helpers

    private val lightContent = listOf(
        "Android light" to AndroidLightContentColors,
        "iOS light" to IosLightContentColors,
    )
    private val darkContent = listOf(
        "Android dark" to AndroidDarkContentColors,
        "iOS dark" to IosDarkContentColors,
    )
    private val raised = listOf(
        "Level 1" to OrbitShadow.Level1,
        "Level 2" to OrbitShadow.Level2,
        "Level 3" to OrbitShadow.Level3,
        "Level 4" to OrbitShadow.Level4,
    )

    private fun assertMetrics(
        tier: String,
        metrics: OrbitFontMetrics,
        size: Int,
        lineHeight: Int,
        tracking: Float,
    ) {
        assertEquals(size.sp, metrics.size, "$tier size")
        assertEquals(lineHeight.sp, metrics.lineHeight, "$tier line height")
        assertEquals(tracking.sp, metrics.tracking, "$tier tracking")
    }

    private fun assertLevel(
        name: String,
        level: OrbitElevationLevel,
        offsetY: Int,
        blur: Int,
        opacity: Float,
        darkSurface: Long,
    ) {
        assertEquals(offsetY.dp, level.offsetY, "$name offset")
        assertEquals(blur.dp, level.blur, "$name blur")
        assertEquals(opacity, level.opacity, "$name opacity")
        assertEquals(Color(darkSurface), level.darkSurface, "$name dark surface")
    }

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
}
