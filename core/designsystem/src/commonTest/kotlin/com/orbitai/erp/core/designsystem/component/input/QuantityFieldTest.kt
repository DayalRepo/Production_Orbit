package com.orbitai.erp.core.designsystem.component.input

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The quantity field's accept rule.
 *
 * Worth its own tests because the empty-string case is the one that breaks naive implementations: a
 * field cleared in order to be retyped is empty for a keystroke, and a rule that treats "not a number"
 * as zero hands the caller a value the user never entered.
 */
class QuantityFieldTest {

    private val range = 1..999

    @Test
    fun `a number inside the range is accepted`() {
        assertTrue(isAcceptableQuantity("1", range), "the floor is inclusive")
        assertTrue(isAcceptableQuantity("250", range))
        assertTrue(isAcceptableQuantity("999", range), "the ceiling is inclusive")
    }

    @Test
    fun `a number outside the range is refused rather than clamped`() {
        assertFalse(isAcceptableQuantity("0", range), "below a range that starts at one")
        assertFalse(isAcceptableQuantity("1000", range))
    }

    @Test
    fun `a draft that is not a number is refused, and an empty draft is not zero`() {
        assertFalse(
            isAcceptableQuantity("", range),
            "a field cleared to be retyped must not report a value",
        )
        assertFalse(isAcceptableQuantity("-", range))
        assertFalse(isAcceptableQuantity("12kg", range))
    }

    @Test
    fun `a zero-based range admits zero`() {
        assertTrue(
            isAcceptableQuantity("0", 0..10),
            "a caller who asks for zero to be reachable means it",
        )
    }
}
