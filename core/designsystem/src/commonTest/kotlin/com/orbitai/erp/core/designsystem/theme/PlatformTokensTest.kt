package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlatformTokensTest {

    private val android = platformTokens(OrbitPlatform.Android)
    private val ios = platformTokens(OrbitPlatform.Ios)

    @Test
    fun `minimum touch targets follow each platform's own guideline`() {
        // 48dp Material, 44pt Apple. Neither is padded to match the other.
        assertEquals(48.dp, android.sizing.minTouchTarget)
        assertEquals(44.dp, ios.sizing.minTouchTarget)
    }

    @Test
    fun `top bar titles follow native alignment conventions`() {
        assertEquals(OrbitTitleAlignment.Start, android.topBarTitleAlignment)
        assertEquals(OrbitTitleAlignment.Center, ios.topBarTitleAlignment)
    }

    @Test
    fun `icon sizes match the system icon sheet`() {
        // The icon spec gives one ladder for both platforms: 16 inline, 24 standard, 32 action,
        // 48 hero. iOS used to step at 20 and 28 where Android plateaued; that divergence is gone,
        // and what remains platform-specific is the touch target rather than the glyph.
        listOf("Android" to android, "iOS" to ios).forEach { (name, tokens) ->
            assertEquals(12.dp, tokens.sizing.iconXs, "$name iconXs")
            assertEquals(16.dp, tokens.sizing.iconSm, "$name iconSm")
            assertEquals(24.dp, tokens.sizing.iconMd, "$name iconMd")
            assertEquals(24.dp, tokens.sizing.iconLg, "$name iconLg")
            assertEquals(32.dp, tokens.sizing.iconXl, "$name iconXl")
            assertEquals(32.dp, tokens.sizing.iconXxl, "$name iconXxl")
            assertEquals(48.dp, tokens.sizing.iconHero, "$name iconHero")
        }
    }

    @Test
    fun `avatar sizes match the avatar sheet`() {
        assertEquals(24.dp, android.sizing.avatarXs)
        assertEquals(40.dp, android.sizing.avatarSm)
        assertEquals(48.dp, android.sizing.avatarMd)
        assertEquals(64.dp, android.sizing.avatarLg)
        assertEquals(88.dp, android.sizing.avatarXl)

        assertEquals(24.dp, ios.sizing.avatarXs)
        assertEquals(32.dp, ios.sizing.avatarSm)
        assertEquals(40.dp, ios.sizing.avatarMd)
        assertEquals(64.dp, ios.sizing.avatarLg)
        assertEquals(80.dp, ios.sizing.avatarXl)
    }

    @Test
    fun `icon and avatar ramps ascend`() {
        listOf("Android" to android, "iOS" to ios).forEach { (name, tokens) ->
            val icons = with(tokens.sizing) {
                listOf(iconXs, iconSm, iconMd, iconLg, iconXl, iconXxl).map { it.value }
            }
            icons.zipWithNext().forEach { (a, b) ->
                assertTrue(b >= a, "$name icon ramp is not ascending: $icons")
            }
            val avatars = with(tokens.sizing) {
                listOf(avatarXs, avatarSm, avatarMd, avatarLg, avatarXl).map { it.value }
            }
            avatars.zipWithNext().forEach { (a, b) ->
                assertTrue(b > a, "$name avatar ramp is not ascending: $avatars")
            }
        }
    }

    @Test
    fun `icon stroke weight sits in the 1_5 to 2 range`() {
        listOf("Android" to android, "iOS" to ios).forEach { (name, tokens) ->
            val stroke = tokens.sizing.iconStrokeWidth.value
            assertTrue(stroke in 1.5f..2.0f, "$name icon stroke is $stroke, expected 1.5–2")
        }
    }

    @Test
    fun `chip visible height never exceeds its touch target`() {
        listOf("Android" to android, "iOS" to ios).forEach { (name, tokens) ->
            assertTrue(
                tokens.sizing.chipHeight <= tokens.sizing.minTouchTarget,
                "$name chip is taller than its own touch target",
            )
        }
    }

    @Test
    fun `every platform is covered by the token lookup`() {
        OrbitPlatform.entries.forEach { platform ->
            assertEquals(platform, platformTokens(platform).platform)
        }
    }
}
