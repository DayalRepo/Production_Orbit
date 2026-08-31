package com.orbitai.erp.core.designsystem.theme

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The avatar tiers, checked against `User Profile Avatars (Android & iOS).xlsx`.
 *
 * This is a transcription, and a transcription's failure mode is a plausible-looking wrong number
 * that no reviewer catches by eye. Pinning the values means the sheet and the code can only disagree
 * loudly. Three of the five tiers genuinely differ between platforms, which is the reason the sizes
 * are resolved through `OrbitTheme.sizing` rather than being constants on `OrbitAvatarSize`.
 */
class AvatarGeometryTest {

    private val android = AndroidPlatformTokens.sizing
    private val ios = IosPlatformTokens.sizing

    @Test
    fun `avatar tiers match the platform spreadsheet`() {
        assertEquals(24.dp, android.avatarXs, "Android XS")
        assertEquals(40.dp, android.avatarSm, "Android SM")
        assertEquals(48.dp, android.avatarMd, "Android MD")
        assertEquals(64.dp, android.avatarLg, "Android LG")
        assertEquals(88.dp, android.avatarXl, "Android XL")

        assertEquals(24.dp, ios.avatarXs, "iOS XS")
        assertEquals(32.dp, ios.avatarSm, "iOS SM")
        assertEquals(40.dp, ios.avatarMd, "iOS MD")
        assertEquals(64.dp, ios.avatarLg, "iOS LG")
        assertEquals(80.dp, ios.avatarXl, "iOS XL")
    }

    @Test
    fun `avatar tiers ascend on both platforms`() {
        // The tiers are named by role rather than by size, so nothing in the type system stops MD
        // being set smaller than SM. `OrbitAvatarSize` is an ordered enum and the gallery renders it
        // in declaration order, so an inverted pair would show as a step backwards.
        listOf("android" to android, "ios" to ios).forEach { (name, sizing) ->
            val tiers = listOf(
                sizing.avatarXs,
                sizing.avatarSm,
                sizing.avatarMd,
                sizing.avatarLg,
                sizing.avatarXl,
            )
            tiers.zipWithNext().forEachIndexed { i, (smaller, larger) ->
                assertTrue(
                    larger > smaller,
                    "$name avatar tier ${i + 1} ($larger) is not larger than tier $i ($smaller)",
                )
            }
        }
    }

    @Test
    fun `the smallest avatar is below the touch target it may need`() {
        // Not a defect — an inline avatar in a comment thread has to be small. It is recorded here
        // because it is the reason `OrbitAvatar` expands its hit area to `minTouchTarget` when it is
        // given an `onClick`, and if the XS tier ever grew past that the expansion would look like
        // dead code and get removed.
        listOf("android" to android, "ios" to ios).forEach { (name, sizing) ->
            assertTrue(
                sizing.avatarXs < sizing.minTouchTarget,
                "$name XS avatar (${sizing.avatarXs}) now meets the touch target, so the hit-area " +
                    "expansion in OrbitAvatar looks redundant — re-derive it before removing it",
            )
        }
    }
}
