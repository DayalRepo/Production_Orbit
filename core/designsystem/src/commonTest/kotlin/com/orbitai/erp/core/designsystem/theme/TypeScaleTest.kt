package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies both platforms' type scales from a single host test.
 *
 * This is the payoff of keeping the token data in `commonMain` and the `expect`/`actual` down to one
 * enum: the iOS scale is checked here on Windows, where the Apple targets cannot even be compiled.
 */
class TypeScaleTest {

    private val scales = listOf(
        "Android" to AndroidTypeScale,
        "iOS" to IosTypeScale,
    )

    @Test
    fun `scales match the specification sheets`() {
        assertEquals(16.sp, AndroidTypeScale.baseSize)
        assertEquals(32.sp, AndroidTypeScale.h1.size)
        assertEquals(40.sp, AndroidTypeScale.h1.lineHeight)
        assertEquals(16.sp, AndroidTypeScale.body.size)
        assertEquals(24.sp, AndroidTypeScale.body.lineHeight)
        assertEquals(12.sp, AndroidTypeScale.caption.size)

        assertEquals(17.sp, IosTypeScale.baseSize)
        assertEquals(34.sp, IosTypeScale.h1.size)
        assertEquals(41.sp, IosTypeScale.h1.lineHeight)
        assertEquals(17.sp, IosTypeScale.body.size)
        assertEquals(22.sp, IosTypeScale.body.lineHeight)
        assertEquals(12.sp, IosTypeScale.caption.size)
    }

    @Test
    fun `both platforms use the agreed 1_125 ratio`() {
        scales.forEach { (name, scale) ->
            assertEquals(1.125f, scale.scaleRatio, "$name scale ratio")
        }
    }

    @Test
    fun `body size equals the platform base size`() {
        scales.forEach { (name, scale) ->
            assertEquals(scale.baseSize, scale.body.size, "$name body should be the base size")
        }
    }

    @Test
    fun `no tier drops below the legibility floor`() {
        scales.forEach { (name, scale) ->
            val tiers = listOf(
                "displayLarge" to scale.displayLarge, "displayMedium" to scale.displayMedium,
                "displaySmall" to scale.displaySmall, "h1" to scale.h1, "h2" to scale.h2,
                "h3" to scale.h3, "h4" to scale.h4, "body" to scale.body,
                "small" to scale.small, "caption" to scale.caption,
            )
            tiers.forEach { (tier, metrics) ->
                assertTrue(
                    metrics.size.value >= scale.minimumSize.value,
                    "$name $tier is ${metrics.size.value}, below the ${scale.minimumSize.value} floor",
                )
            }
        }
    }

    @Test
    fun `sizes descend monotonically from display to caption`() {
        scales.forEach { (name, scale) ->
            val ordered = listOf(
                scale.displayLarge, scale.displayMedium, scale.displaySmall,
                scale.h1, scale.h2, scale.h3, scale.h4,
                scale.body, scale.small, scale.caption,
            ).map { it.size.value }
            ordered.zipWithNext().forEach { (larger, smaller) ->
                assertTrue(larger >= smaller, "$name scale is not descending: $ordered")
            }
        }
    }

    @Test
    fun `line height always exceeds font size`() {
        scales.forEach { (name, scale) ->
            val tiers = listOf(
                scale.displayLarge, scale.displayMedium, scale.displaySmall,
                scale.h1, scale.h2, scale.h3, scale.h4,
                scale.body, scale.small, scale.caption,
            )
            tiers.forEach { m ->
                assertTrue(
                    m.lineHeight.value > m.size.value,
                    "$name has lineHeight ${m.lineHeight.value} <= size ${m.size.value}",
                )
            }
        }
    }

    @Test
    fun `long-form body line height is at least 1_5x the font size`() {
        scales.forEach { (name, scale) ->
            val tokens = orbitTypographyTokens(FontFamily.Default, scale)
            val ratio = tokens.bodyLongForm.lineHeight.value / tokens.bodyLongForm.fontSize.value
            assertTrue(
                ratio >= 1.5f - 0.001f,
                "$name bodyLongForm ratio is $ratio, WCAG 1.4.12 wants >= 1.5",
            )
        }
    }

    @Test
    fun `every Material style carries zero tracking except none`() {
        // "Spacing by default: 0" — Material's Roboto-tuned tracking is not inherited.
        scales.forEach { (name, scale) ->
            val t = orbitTypography(FontFamily.Default, scale)
            val styles = listOf(
                "displayLarge" to t.displayLarge, "headlineLarge" to t.headlineLarge,
                "titleLarge" to t.titleLarge, "titleMedium" to t.titleMedium,
                "bodyLarge" to t.bodyLarge, "bodyMedium" to t.bodyMedium,
                "bodySmall" to t.bodySmall, "labelLarge" to t.labelLarge,
                "labelMedium" to t.labelMedium, "labelSmall" to t.labelSmall,
            )
            styles.forEach { (label, style) ->
                assertTrue(
                    abs(style.letterSpacing.value) < 0.001f,
                    "$name $label has tracking ${style.letterSpacing.value}, expected 0",
                )
            }
        }
    }

    @Test
    fun `labelSmall is not below the caption tier`() {
        // Regression guard: this was 11sp, under both platforms' stated minimum.
        scales.forEach { (name, scale) ->
            val t = orbitTypography(FontFamily.Default, scale)
            assertEquals(
                scale.caption.size, t.labelSmall.fontSize,
                "$name labelSmall must not undercut the caption tier",
            )
        }
    }

    @Test
    fun `numeric styles request tabular figures`() {
        scales.forEach { (name, scale) ->
            val tokens = orbitTypographyTokens(FontFamily.Default, scale)
            listOf(
                "tableNumeric" to tokens.tableNumeric,
                "reference" to tokens.reference,
                "metricLarge" to tokens.metricLarge,
            ).forEach { (label, style) ->
                assertEquals(
                    TabularNumbers, style.fontFeatureSettings,
                    "$name $label should request tabular figures so columns align",
                )
            }
        }
    }

    @Test
    fun `platform lookup returns the matching token set`() {
        assertEquals(AndroidTypeScale, platformTokens(OrbitPlatform.Android).typeScale)
        assertEquals(IosTypeScale, platformTokens(OrbitPlatform.Ios).typeScale)
    }
}
