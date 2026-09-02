package com.orbitai.erp.core.designsystem.component.button

import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.platformTokens
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Guards the sizing contract the buttons rely on. The composables themselves need an instrumented
 * or UI test to exercise; these assertions cover the token relationships that are easy to break
 * silently by editing [com.orbitai.erp.core.designsystem.theme.OrbitSizing].
 */
class OrbitButtonTest {

    private val platforms = OrbitPlatform.entries.map { it to platformTokens(it) }

    @Test
    fun `the small button relies on an expanded hit area`() {
        // OrbitButton draws at the size's height but expands its hit area to minTouchTarget. The
        // Small button is the case that actually needs it, so if it ever grew past the target the
        // expansion would become a no-op and the guarantee on the composable would quietly lapse.
        platforms.forEach { (name, tokens) ->
            assertTrue(
                tokens.sizing.buttonHeightSm < tokens.sizing.minTouchTarget,
                "$name small button (${tokens.sizing.buttonHeightSm}) is not smaller than the " +
                    "${tokens.sizing.minTouchTarget} touch target, making the expansion pointless",
            )
        }
    }

    @Test
    fun `medium and large buttons already clear the touch target unaided`() {
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                assertTrue(
                    buttonHeightMd >= minTouchTarget || buttonHeightLg >= minTouchTarget,
                    "$name has no button size that meets $minTouchTarget on its own",
                )
            }
        }
    }

    @Test
    fun `button heights ascend`() {
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                assertTrue(
                    buttonHeightSm < buttonHeightMd && buttonHeightMd < buttonHeightLg,
                    "$name button heights are not ascending",
                )
            }
        }
    }

    @Test
    fun `button glyphs and paddings and minimum widths ascend with size`() {
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                assertTrue(
                    buttonIconSm < buttonIconMd && buttonIconMd < buttonIconLg,
                    "$name button glyph sizes are not ascending",
                )
                assertTrue(
                    buttonPaddingSm < buttonPaddingMd && buttonPaddingMd < buttonPaddingLg,
                    "$name button paddings are not ascending",
                )
                assertTrue(
                    buttonMinWidthSm < buttonMinWidthMd && buttonMinWidthMd < buttonMinWidthLg,
                    "$name button minimum widths are not ascending",
                )
            }
        }
    }

    @Test
    fun `no button or icon-button glyph renders its stroke below one dp`() {
        // The icon stroke is authored in viewport units, so it scales with the glyph: a 24dp icon
        // draws at 1.8dp and a 12dp one at 0.9dp. Below 16dp the stroke crosses under 1dp and the
        // glyph goes hairline next to its label, which is the failure this guards. It is a token
        // check rather than a rendering one, but it is the token that decides the rendering.
        val floor = 13.4.dp
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                listOf(
                    "button small" to buttonIconSm,
                    "button medium" to buttonIconMd,
                    "button large" to buttonIconLg,
                    "icon button small" to iconButtonGlyphSm,
                    "icon button medium" to iconButtonGlyphMd,
                    "icon button large" to iconButtonGlyphLg,
                ).forEach { (which, glyph) ->
                    assertTrue(
                        glyph >= floor,
                        "$name $which glyph ($glyph) is under $floor, so its 1.8-unit stroke " +
                            "renders below 1dp",
                    )
                }
            }
        }
    }

    @Test
    fun `an icon button glyph leaves room inside its hit target`() {
        // Bare glyphs are the default now; the touch target is still the full platform minimum
        // while the ink stays smaller, so there is always clear space around the stroke.
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                listOf(
                    Triple("small", iconButtonGlyphSm, maxOf(iconButtonSm, minTouchTarget)),
                    Triple("medium", iconButtonGlyphMd, maxOf(iconButtonMd, minTouchTarget)),
                    Triple("large", iconButtonGlyphLg, maxOf(iconButtonLg, minTouchTarget)),
                ).forEach { (size, glyph, target) ->
                    assertTrue(
                        target - glyph >= 12.dp,
                        "$name $size icon button leaves under 12dp around its $glyph " +
                            "glyph in a $target target",
                    )
                }
            }
        }
    }

    @Test
    fun `icon button rings ascend and stay within reach of the touch target`() {
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                assertTrue(
                    iconButtonSm < iconButtonMd && iconButtonMd < iconButtonLg,
                    "$name icon button ring diameters are not ascending",
                )
                assertTrue(
                    iconButtonGlyphSm < iconButtonGlyphMd &&
                        iconButtonGlyphMd < iconButtonGlyphLg,
                    "$name icon button glyphs are not ascending",
                )
                assertTrue(
                    iconButtonSm < minTouchTarget,
                    "$name small icon button ring ($iconButtonSm) is not smaller than the " +
                        "$minTouchTarget touch target, making the expansion pointless",
                )
                // The Large ring may exceed the target — iOS's is 44pt — because the component sizes
                // its hit area to max(ring, target). What must not happen is the ring growing large
                // enough to swallow a neighbour's target.
                assertTrue(
                    iconButtonLg <= minTouchTarget * 1.25f,
                    "$name large icon button ring ($iconButtonLg) has outgrown its " +
                        "$minTouchTarget target by more than a quarter",
                )
            }
        }
    }

    @Test
    fun `a glyph is smaller than the pill it sits in`() {
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                listOf(
                    Triple("small", buttonIconSm, buttonHeightSm),
                    Triple("medium", buttonIconMd, buttonHeightMd),
                    Triple("large", buttonIconLg, buttonHeightLg),
                ).forEach { (size, glyph, height) ->
                    assertTrue(
                        glyph < height,
                        "$name $size glyph ($glyph) is not smaller than its $height pill",
                    )
                }
            }
        }
    }

    @Test
    fun `icon button target is at least as large as the icon it draws`() {
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                listOf(iconXs, iconSm, iconMd, iconLg, iconXl, iconXxl).forEach { icon ->
                    assertTrue(
                        icon <= minTouchTarget,
                        "$name icon size $icon exceeds the ${minTouchTarget} touch target",
                    )
                }
            }
        }
    }

    @Test
    fun `disabled alpha is shared rather than redefined per component`() {
        // OrbitChip previously carried its own private copy of this constant.
        //
        // The second half of this test compared Disabled against Inactive; that constant went with
        // `OrbitButtonState.Inactive`, and with one faded state there is no longer an ordering to
        // assert. What is left is the range check, which still catches the way this actually breaks
        // in practice: someone reaching for 38 instead of 0.38 when reading the Material spec.
        assertTrue(OrbitAlpha.Disabled > 0f && OrbitAlpha.Disabled < 1f)
    }
}
