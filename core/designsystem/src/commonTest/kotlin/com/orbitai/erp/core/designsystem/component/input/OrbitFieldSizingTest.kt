package com.orbitai.erp.core.designsystem.component.input

import com.orbitai.erp.core.designsystem.theme.AndroidPlatformTokens
import com.orbitai.erp.core.designsystem.theme.IosPlatformTokens
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The sizing rules a text field has to keep on both platforms.
 *
 * These are cheap assertions guarding an expensive mistake. Field geometry is the kind of thing
 * that gets nudged during a visual pass — a few dp off a height to make a form look tighter — and
 * the resulting control is still perfectly usable for whoever nudged it while being unhittable for
 * someone with a motor impairment.
 */
class OrbitFieldSizingTest {

    private val platforms = listOf(
        "android" to AndroidPlatformTokens.sizing,
        "ios" to IosPlatformTokens.sizing,
    )

    @Test
    fun `every field size clears the platform touch minimum`() {
        // Unlike buttons, where Small is deliberately under the minimum for dense table rows. A
        // button that is hard to hit can be hit on the second try; a field that is hard to hit
        // cannot be typed into at all, so there is no case that justifies going under here.
        platforms.forEach { (platform, sizing) ->
            listOf(
                "small" to sizing.fieldHeightSm,
                "medium" to sizing.fieldHeightMd,
                "large" to sizing.fieldHeightLg,
            ).forEach { (name, height) ->
                assertTrue(
                    height >= sizing.minTouchTarget,
                    "$platform $name field is $height, under the ${sizing.minTouchTarget} minimum",
                )
            }
        }
    }

    @Test
    fun `field heights increase with size`() {
        platforms.forEach { (platform, sizing) ->
            assertTrue(
                sizing.fieldHeightSm < sizing.fieldHeightMd &&
                    sizing.fieldHeightMd < sizing.fieldHeightLg,
                "$platform field heights are not strictly increasing",
            )
            assertTrue(
                sizing.fieldPaddingSm < sizing.fieldPaddingMd &&
                    sizing.fieldPaddingMd < sizing.fieldPaddingLg,
                "$platform field padding does not track its heights",
            )
        }
    }

    @Test
    fun `a field is never shorter than a button of the same size`() {
        // A form is usually a column of fields ending in a button, and a button taller than the
        // fields above it makes the whole column look like it was assembled from two designs.
        platforms.forEach { (platform, sizing) ->
            assertTrue(
                sizing.fieldHeightMd >= sizing.buttonHeightMd,
                "$platform medium field (${sizing.fieldHeightMd}) is shorter than its medium " +
                    "button (${sizing.buttonHeightMd})",
            )
        }
    }
}
