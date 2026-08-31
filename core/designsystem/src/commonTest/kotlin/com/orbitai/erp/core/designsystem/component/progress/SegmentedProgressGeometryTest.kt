package com.orbitai.erp.core.designsystem.component.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The arithmetic behind the slats.
 *
 * Worth testing away from the renderer because both functions encode a judgement that plain
 * rounding gets wrong, and both failures are silent — the bar still draws, it just says something
 * that is not true.
 */
class SegmentedProgressGeometryTest {

    private val count = 28

    @Test
    fun `an empty bar is only empty at zero`() {
        assertEquals(0, litSegments(0f, count))
        // 1% rounds to 0 of 28 on the nose. Lighting nothing would say "not started" about work that
        // has started, which on a site report is the difference between chasing a subcontractor and
        // not.
        assertEquals(1, litSegments(0.01f, count))
        assertEquals(1, litSegments(0.0001f, count))
    }

    @Test
    fun `a full bar is only full at one`() {
        assertEquals(count, litSegments(1f, count))
        // 99.4% rounds to 28 of 28. Lighting everything would say "done", and somebody signing off a
        // handover checklist is looking for exactly that signal.
        assertEquals(count - 1, litSegments(0.994f, count))
        assertEquals(count - 1, litSegments(0.999f, count))
    }

    @Test
    fun `the middle rounds to nearest`() {
        assertEquals(14, litSegments(0.5f, count))
        assertEquals(20, litSegments(0.71f, count))
        assertEquals(7, litSegments(0.25f, count))
    }

    @Test
    fun `lit count never exceeds the slat count and never goes negative`() {
        listOf(1, 4, 12, 28, 60).forEach { n ->
            var previous = 0
            (0..100).forEach { step ->
                val lit = litSegments(step / 100f, n)
                assertTrue(lit in 0..n, "$lit of $n is out of range at $step%")
                // Monotonic. A bar that ever goes backwards as its value rises is the kind of thing
                // nobody notices in a static gallery and everybody notices in an animation.
                assertTrue(lit >= previous, "lit count fell from $previous to $lit at $step% of $n")
                previous = lit
            }
        }
    }

    @Test
    fun `a single slat degenerates cleanly`() {
        assertEquals(0, litSegments(0f, 1))
        assertEquals(1, litSegments(1f, 1))
        // With one slat there is no room for "started but not finished", and the clamp would
        // otherwise ask for a value in 1..0. It must not throw.
        assertEquals(1, litSegments(0.5f, 1))
    }

    @Test
    fun `slats are dropped rather than squeezed below the minimum`() {
        val min = 2f
        val gap = 3f

        // Roomy: the request is honoured untouched.
        assertEquals(28, affordableSegments(width = 400f, minWidth = min, gap = gap, requested = 28))

        // Tight: 28 slats would need 28*2 + 27*3 = 137. At 100 wide only 20 fit.
        assertEquals(20, affordableSegments(width = 100f, minWidth = min, gap = gap, requested = 28))

        // Never invents slats to fill a wide screen.
        assertEquals(8, affordableSegments(width = 4000f, minWidth = min, gap = gap, requested = 8))
    }

    @Test
    fun `the fitted count actually fits`() {
        val min = 2f
        val gap = 3f
        listOf(10f, 37f, 100f, 259f, 1000f).forEach { width ->
            val n = affordableSegments(width, min, gap, requested = 28)
            val needed = n * min + (n - 1) * gap
            assertTrue(
                needed <= width || n == 1,
                "$n slats need $needed at width $width — they do not fit",
            )
        }
    }

    @Test
    fun `a degenerate width does not produce a degenerate count`() {
        // A zero-width Canvas happens for a frame during layout. Returning 0 lets the caller bail
        // before dividing by it.
        assertEquals(0, affordableSegments(width = 0f, minWidth = 2f, gap = 3f, requested = 28))
        assertEquals(0, affordableSegments(width = -5f, minWidth = 2f, gap = 3f, requested = 28))
        // Narrower than one slat still yields one rather than zero, so the bar degrades to a sliver
        // instead of vanishing.
        assertEquals(1, affordableSegments(width = 1f, minWidth = 2f, gap = 3f, requested = 28))
    }
}
