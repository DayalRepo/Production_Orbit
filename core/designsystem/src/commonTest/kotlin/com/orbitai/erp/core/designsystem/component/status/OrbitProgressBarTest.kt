package com.orbitai.erp.core.designsystem.component.status

import kotlin.test.Test
import kotlin.test.assertEquals

class OrbitProgressBarTest {

    @Test
    fun `passes through fractions already in range`() {
        assertEquals(0f, normalizeProgress(0f))
        assertEquals(0.42f, normalizeProgress(0.42f))
        assertEquals(1f, normalizeProgress(1f))
    }

    @Test
    fun `clamps out-of-range fractions instead of throwing`() {
        assertEquals(0f, normalizeProgress(-0.5f))
        assertEquals(1f, normalizeProgress(1.4f))
        assertEquals(1f, normalizeProgress(Float.POSITIVE_INFINITY))
        assertEquals(0f, normalizeProgress(Float.NEGATIVE_INFINITY))
    }

    @Test
    fun `maps NaN to zero`() {
        // A completion percentage computed as 0 done / 0 total arrives as NaN, and NaN would
        // otherwise propagate into fillMaxWidth and throw.
        assertEquals(0f, normalizeProgress(Float.NaN))
    }
}
