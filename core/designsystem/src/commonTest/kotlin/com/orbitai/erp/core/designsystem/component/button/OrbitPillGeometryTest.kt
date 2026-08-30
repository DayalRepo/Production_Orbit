package com.orbitai.erp.core.designsystem.component.button

import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import com.orbitai.erp.core.designsystem.theme.platformTokens
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Guards the geometry a pill needs and a rounded rectangle does not.
 *
 * A pill's corner radius is half its height, which changes what "wide enough" means. On a rounded
 * rectangle the label only has to clear the padding; on a pill it also has to clear the curve, and a
 * short label on a narrow pill produces a lozenge that no longer reads as a button. Since the
 * buttons are now text-only, there is no glyph to pad the label out, so these floors are the only
 * thing holding the shape.
 */
class OrbitPillGeometryTest {

    private val platforms = OrbitPlatform.entries.map { it to platformTokens(it) }

    @Test
    fun `every pill is wider than it is tall`() {
        // The degenerate case: a minimum width at or below the height makes the pill a circle, and
        // "Open" inside a circle is a badge, not a button.
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                listOf(
                    "small" to (buttonMinWidthSm to buttonHeightSm),
                    "medium" to (buttonMinWidthMd to buttonHeightMd),
                    "large" to (buttonMinWidthLg to buttonHeightLg),
                ).forEach { (size, dims) ->
                    val (width, height) = dims
                    assertTrue(
                        width > height * MinAspect,
                        "$name $size pill is $width by $height, too square to read as a button",
                    )
                }
            }
        }
    }

    @Test
    fun `the minimum width is a floor and not a straitjacket`() {
        // Two bounds, and the upper one is the point of the test.
        //
        // Buttons size to their labels, so the minimum exists only to stop a two-character label
        // collapsing into a circle. It therefore has to leave *some* room between the curves — a
        // floor entirely consumed by padding is no floor at all — while staying small enough that a
        // real label like "Approve" clears it. If the minimum grew past what an ordinary verb needs,
        // every button in a row would be pinned to the same width and the row would go back to
        // reading as a set of identical slabs, which is exactly what sizing to the label avoids.
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                listOf(
                    "small" to Triple(buttonMinWidthSm, buttonPaddingSm, buttonHeightSm),
                    "medium" to Triple(buttonMinWidthMd, buttonPaddingMd, buttonHeightMd),
                    "large" to Triple(buttonMinWidthLg, buttonPaddingLg, buttonHeightLg),
                ).forEach { (size, dims) ->
                    val (width, padding, height) = dims
                    val forLabel = width - padding * 2
                    assertTrue(
                        forLabel >= height * MinLabelRoom,
                        "$name $size pill leaves only $forLabel between its curves, not enough for " +
                            "the two or three characters the floor exists to protect",
                    )
                    assertTrue(
                        forLabel <= height * MaxLabelRoom,
                        "$name $size pill reserves $forLabel for a label before it has one, which " +
                            "pins ordinary verbs to a shared width instead of letting each size " +
                            "to its own",
                    )
                }
            }
        }
    }

    @Test
    fun `padding grows with the pill it wraps`() {
        // A pill's curve scales with its height, so constant end padding would look generous on the
        // small button and mean on the large one.
        platforms.forEach { (name, tokens) ->
            with(tokens.sizing) {
                assertTrue(
                    buttonPaddingSm < buttonPaddingMd && buttonPaddingMd < buttonPaddingLg,
                    "$name button padding does not ascend with size",
                )
            }
        }
    }
}

/**
 * How much wider than tall a pill must be.
 *
 * 1.5 rather than a bare 1.0: at parity the shape is a circle, and it takes roughly half again as
 * much width before the straight section between the two curves is long enough to be visible.
 */
private const val MinAspect = 1.5f

/** Room between the curves, as a fraction of the button's height. See the test that uses these. */
private const val MinLabelRoom = 0.7f
private const val MaxLabelRoom = 1.4f
