package com.orbitai.erp.core.designsystem.component.progress

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Pins the *shape* of a slat rather than any single token.
 *
 * The request that produced these numbers was "narrower bars with clear gaps", and the trap is that
 * neither half of that is expressible as an assertion on a constant: slat width is derived from the
 * container, and the gap is only meaningful relative to the width it sits beside. A test on
 * `SegmentCount == 36` would pass while someone halved the gap and put the solid-looking bar back.
 *
 * So these assert the two ratios that actually decide how the bar reads, at the widths it is
 * actually drawn at.
 */
class SegmentedProgressLayoutTest {

    // A phone card's inner width and a small tablet's, in px at 3x — the two ends of the range this
    // has to look right across.
    private val widths = listOf(330f * 3, 700f * 3)
    private val gap = 1.5f * 3
    private val count = OrbitProgressDefaults.SegmentCount

    private fun slat(width: Float) = (width - gap * (count - 1)) / count

    @Test
    fun `a slat stays wider than the gap beside it`() {
        // The ceiling from the component's own doc: once gaps are as wide as slats the eye stops
        // resolving them as separate objects and the bar becomes a hatched texture, which throws
        // away the entire reason for drawing slats instead of a solid fill.
        widths.forEach { width ->
            assertTrue(
                slat(width) > gap,
                "at ${width}px a slat is ${slat(width)}px against a ${gap}px gap",
            )
        }
    }

    @Test
    fun `the gap is a visible fraction of a slat rather than a hairline`() {
        // The floor at the other end. Below about a quarter the separation survives only as a seam
        // and the run reads as one continuous bar — which is what the redesign was asked to fix.
        //
        // Phone widths only, and that limit is a real property of the component rather than a
        // convenience. The count is fixed so that the same value lights the same number of slats on
        // every device, which means a wider container spends all of its extra width on wider slats
        // while the gap stays put — so this ratio necessarily falls as the screen grows. Asserting
        // it on a tablet would be asserting against the component's own stated trade-off. If wide
        // layouts ever need thinner slats, the fix is a second `segmentCount` at a breakpoint, not
        // a smaller gap everywhere.
        val phone = widths.first()
        assertTrue(
            gap / slat(phone) > 0.25f,
            "at ${phone}px the gap is only ${gap / slat(phone)} of a slat",
        )
    }

    @Test
    fun `a slat on a phone stays inside the band that reads as a slat`() {
        // Bounded from both sides, because the two ends are different failures and the component has
        // now been pushed toward each of them in turn.
        //
        // The ceiling is the chunky bar this design moved away from -- at 32 slats and a hairline
        // gap a phone slat was near 9dp and the run read as a solid fill with scratches in it. The
        // floor is the opposite: much under 4dp and the slat is thinner than the rounding error to
        // physical pixels on a 2x screen, so some slats render a pixel wider than others and the
        // rhythm stutters visibly even though every number here is correct.
        val phone = slat(widths.first()) / 3f
        assertTrue(phone < 7f, "phone slat is ${phone}dp, back into chunky territory")
        assertTrue(phone > 4f, "phone slat is ${phone}dp, too thin to render evenly")
    }

    /**
     * The count has to divide 100, and that is a legibility requirement rather than a tidiness one.
     *
     * The bar is always drawn beside the exact figure, so the two are compared constantly. When one
     * slat is not worth a whole number of percent, neighbouring percentages collapse onto the same
     * slat count -- at 48 slats both 24% and 25% lit twelve -- and the bar appears not to respond
     * to a change the number clearly shows. At 50 every even percentage is a slat boundary.
     */
    @Test
    fun `one slat is worth a whole number of percent`() {
        assertTrue(
            100 % count == 0,
            "$count slats puts one slat at ${100f / count}%, so the bar and the printed figure " +
                "will disagree about which slat a given percentage reaches",
        )
    }

    /**
     * Every even percentage lands exactly on its own slat, with no rounding involved.
     *
     * This is the 2%-per-slat claim stated as a test rather than as a comment. It guards the join
     * between the count above and `litSegments`: a clean divisor is necessary for the mapping to be
     * exact but not sufficient, since the rounding could still be applied in a way that shifts every
     * reading by one.
     *
     * Only the even values, and that is the point rather than a gap in coverage. An odd percentage
     * is exactly half a slat and genuinely has no exact answer -- 3% is 1.5 slats -- so the useful
     * guarantee is that it resolves to one of the two slats either side, which the next test covers.
     * The two extremes are excluded because they are deliberately overridden; `litSegments` has its
     * own tests for those.
     */
    @Test
    fun `each even percent lands exactly on its slat`() {
        (2..98 step 2).forEach { percent ->
            val expected = percent * count / 100
            val actual = litSegments(percent / 100f, count)
            assertTrue(
                actual == expected,
                "$percent% lights $actual of $count slats, expected exactly $expected",
            )
        }
    }

    /**
     * An odd percentage never lands further than one slat from the honest position.
     *
     * The other half of the mapping. Half a slat has to round somewhere, and all that matters is
     * that it rounds to a neighbour rather than drifting -- a bar that is two slats out from the
     * figure printed beside it reads as broken even though both are within their own tolerance.
     */
    @Test
    fun `each odd percent lands on one of the two slats beside it`() {
        (1..99 step 2).forEach { percent ->
            val exact = percent * count / 100f
            val actual = litSegments(percent / 100f, count)
            assertTrue(
                actual >= exact - 1f && actual <= exact + 1f,
                "$percent% is $exact slats but lights $actual",
            )
        }
    }

    @Test
    fun `every slat is above the minimum width at the narrowest sensible container`() {
        // Below `progressSegmentMinWidth` the component silently drops slats, which changes how
        // many light up for the same value. Fine as a safety net, wrong as a normal outcome.
        val narrow = 280f * 3
        assertTrue(
            affordableSegments(narrow, 3f * 3, gap, count) == count,
            "the container is dropping slats at ${narrow / 3}dp",
        )
    }
}
