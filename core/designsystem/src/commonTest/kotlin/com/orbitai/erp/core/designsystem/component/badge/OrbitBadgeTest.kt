package com.orbitai.erp.core.designsystem.component.badge

import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import com.orbitai.erp.core.designsystem.theme.platformTokens
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The badge sizing contract.
 *
 * A badge is not interactive, so unlike a button it is not held to a 48dp/44pt touch target — that
 * is why these heights are allowed to be small. What it *is* held to is having room for its glyph
 * and never being given a fixed height, and those are the two things that break silently.
 */
class OrbitBadgeTest {

    private val platforms = OrbitPlatform.entries.map { it to platformTokens(it).sizing }

    @Test
    fun `badge heights ascend with size`() {
        platforms.forEach { (platform, sizing) ->
            assertTrue(
                sizing.badgeHeightSm < sizing.badgeHeightMd &&
                    sizing.badgeHeightMd < sizing.badgeHeightLg,
                "$platform badge heights do not ascend: ${sizing.badgeHeightSm}, " +
                    "${sizing.badgeHeightMd}, ${sizing.badgeHeightLg}",
            )
        }
    }

    @Test
    fun `badge icon sizes ascend with size`() {
        platforms.forEach { (platform, sizing) ->
            assertTrue(
                sizing.badgeIconSm < sizing.badgeIconMd &&
                    sizing.badgeIconMd < sizing.badgeIconLg,
                "$platform badge icon sizes do not ascend",
            )
        }
    }

    @Test
    fun `each badge height leaves vertical padding around its glyph`() {
        // A pill exactly as tall as its icon reads as a cropped circle. Four dp of combined
        // padding is the minimum that still looks like a container.
        val minimumPadding = 4
        platforms.forEach { (platform, sizing) ->
            val pairs = listOf(
                "Small" to (sizing.badgeHeightSm to sizing.badgeIconSm),
                "Medium" to (sizing.badgeHeightMd to sizing.badgeIconMd),
                "Large" to (sizing.badgeHeightLg to sizing.badgeIconLg),
            )
            pairs.forEach { (name, dims) ->
                val (height, icon) = dims
                assertTrue(
                    height.value - icon.value >= minimumPadding,
                    "$platform $name badge is ${height.value}dp around a ${icon.value}dp glyph",
                )
            }
        }
    }

    @Test
    fun `badges stay below the touch target because they are display-only`() {
        // If a badge ever needs to be tappable it must gain an expanded hit area the way
        // OrbitChip did, rather than being grown to 48dp and wrecking the density of a card.
        platforms.forEach { (platform, sizing) ->
            assertTrue(
                sizing.badgeHeightLg < sizing.minTouchTarget,
                "$platform largest badge (${sizing.badgeHeightLg}) reaches the touch target; " +
                    "if badges became interactive this test should be replaced, not deleted",
            )
        }
    }

    @Test
    fun `every emphasis and size is covered`() {
        assertTrue(OrbitBadgeEmphasis.entries.size == 3, "expected Glass, Solid, Outline")
        assertTrue(OrbitBadgeSize.entries.size == 3, "expected Small, Medium, Large")
    }
}
