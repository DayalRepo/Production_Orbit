package com.orbitai.erp.core.designsystem.component.progress

import com.orbitai.erp.core.designsystem.foundation.OrbitPlatform
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.platformTokens
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Phase arithmetic, clamping and compact sizing for [OrbitStepIndicator].
 *
 * Kept off the renderer for the same reason as the segmented-progress geometry tests: a silent
 * off-by-one in "which stage is current" is worse than a crash, and it will not show up in a
 * screenshot of the happy path.
 */
class OrbitStepIndicatorTest {

    @Test
    fun `phases split cleanly around the current index`() {
        assertEquals(OrbitStepPhase.Completed, orbitStepPhase(0, current = 2))
        assertEquals(OrbitStepPhase.Completed, orbitStepPhase(1, current = 2))
        assertEquals(OrbitStepPhase.Current, orbitStepPhase(2, current = 2))
        assertEquals(OrbitStepPhase.Upcoming, orbitStepPhase(3, current = 2))
        assertEquals(OrbitStepPhase.Upcoming, orbitStepPhase(4, current = 2))
    }

    @Test
    fun `current index is clamped into the list`() {
        assertEquals(0, orbitStepCurrentIndex(-3, size = 5))
        assertEquals(0, orbitStepCurrentIndex(0, size = 5))
        assertEquals(4, orbitStepCurrentIndex(4, size = 5))
        assertEquals(4, orbitStepCurrentIndex(99, size = 5))
        assertEquals(0, orbitStepCurrentIndex(0, size = 0))
        assertEquals(0, orbitStepCurrentIndex(5, size = 1))
    }

    @Test
    fun `step glyphs stay smaller than toolbar icons`() {
        OrbitPlatform.entries.forEach { platform ->
            val sizing = platformTokens(platform).sizing
            assertTrue(
                sizing.stepGlyphSize <= sizing.iconMd,
                "$platform step glyph (${sizing.stepGlyphSize}) exceeds toolbar size",
            )
            assertTrue(
                sizing.stepNodeSize <= sizing.stepGlyphSize,
                "$platform step node larger than its glyph",
            )
            assertTrue(
                sizing.stepColumnWidth < sizing.minTouchTarget,
                "$platform step column should stay denser than a touch target; the card is the hit area",
            )
            assertTrue(
                sizing.stepRowMinHeight < sizing.minTouchTarget,
                "$platform step row (${sizing.stepRowMinHeight}) is not compact enough",
            )
        }
    }

    @Test
    fun `solid rail has thickness and length`() {
        OrbitPlatform.entries.forEach { platform ->
            val sizing = platformTokens(platform).sizing
            assertTrue(
                sizing.stepRailThickness.value > 0f,
                "$platform rail thickness must be visible",
            )
            assertTrue(
                sizing.stepRailLength.value >= sizing.stepGlyphSize.value * 0.5f,
                "$platform rail too short to read as a connector",
            )
        }
    }

    @Test
    fun `numbered square icons map one through five`() {
        assertSame(OrbitIcons.OneSquare, orbitStepSquareIcon(0))
        assertSame(OrbitIcons.TwoSquare, orbitStepSquareIcon(1))
        assertSame(OrbitIcons.ThreeSquare, orbitStepSquareIcon(2))
        assertSame(OrbitIcons.FourSquare, orbitStepSquareIcon(3))
        assertSame(OrbitIcons.FiveSquare, orbitStepSquareIcon(4))
        assertSame(OrbitIcons.FiveSquare, orbitStepSquareIcon(9))
    }

    @Test
    fun `step carries optional status text and dates without requiring them`() {
        val bare = OrbitStep(label = "Inspection")
        assertEquals(null, bare.statusLabel)
        assertEquals(null, bare.startedOn)

        val full = OrbitStep(
            label = "Approval",
            statusLabel = "Rejected",
            startedOn = "27/08/2026",
            endedOn = "28/08/2026",
        )
        assertEquals("Rejected", full.statusLabel)
        assertEquals("27/08/2026", full.startedOn)
        assertEquals("28/08/2026", full.endedOn)
    }

    @Test
    fun `digits are one-based and wrap after nine`() {
        assertEquals("1", orbitStepDigit(0))
        assertEquals("5", orbitStepDigit(4))
        assertEquals("1", orbitStepDigit(9))
    }

    @Test
    fun `progress summary is one-based over total`() {
        assertEquals("0/0 stages", orbitStepProgressSummary(0, total = 0))
        assertEquals("1/5 stages", orbitStepProgressSummary(0, total = 5))
        assertEquals("2/5 stages", orbitStepProgressSummary(1, total = 5))
        assertEquals("5/5 stages", orbitStepProgressSummary(99, total = 5))
    }

    @Test
    fun `total days span the earliest start and latest end`() {
        val steps = listOf(
            OrbitStep(label = "Scheduled", startedOn = "15/08/2026", endedOn = "16/08/2026"),
            OrbitStep(label = "In progress", startedOn = "16/08/2026", endedOn = "20/08/2026"),
            OrbitStep(label = "Completed", startedOn = "23/08/2026", endedOn = "23/08/2026"),
        )
        assertEquals(9, orbitStepTotalDays(steps))
        assertEquals("9 days", orbitStepTotalDaysLabel(steps))
        assertEquals("—", orbitStepTotalDaysLabel(listOf(OrbitStep(label = "Scheduled"))))
    }

    @Test
    fun `workflow complete fills every stage mark`() {
        val steps = listOf(
            OrbitStep(label = "Scheduled", statusLabel = "Started"),
            OrbitStep(label = "Completed", statusLabel = "Complete"),
        )
        assertEquals(true, orbitWorkflowComplete(steps, currentIndex = 1))
        assertEquals(OrbitStepPhase.Completed, orbitStepPhase(0, 1, steps))
        assertEquals(OrbitStepPhase.Completed, orbitStepPhase(1, 1, steps))
    }

    @Test
    fun `in-progress workflow keeps current stage dashed`() {
        val steps = listOf(
            OrbitStep(label = "Scheduled", statusLabel = "Started"),
            OrbitStep(label = "In progress", statusLabel = "In progress"),
        )
        assertEquals(false, orbitWorkflowComplete(steps, currentIndex = 1))
        assertEquals(OrbitStepPhase.Completed, orbitStepPhase(0, 1, steps))
        assertEquals(OrbitStepPhase.Current, orbitStepPhase(1, 1, steps))
    }
}
