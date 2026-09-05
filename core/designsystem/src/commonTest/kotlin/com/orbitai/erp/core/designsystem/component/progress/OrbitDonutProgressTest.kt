package com.orbitai.erp.core.designsystem.component.progress

import kotlin.test.Test
import kotlin.test.assertEquals

class OrbitDonutProgressTest {

    @Test
    fun `percent label rounds a clamped fraction to a whole number`() {
        assertEquals(0, OrbitDonutProgressDefaults.percentLabel(0f))
        assertEquals(78, OrbitDonutProgressDefaults.percentLabel(0.78f))
        assertEquals(100, OrbitDonutProgressDefaults.percentLabel(1f))
    }

    @Test
    fun `percent label clamps values outside zero to one`() {
        assertEquals(0, OrbitDonutProgressDefaults.percentLabel(-0.4f))
        assertEquals(100, OrbitDonutProgressDefaults.percentLabel(1.4f))
    }

    @Test
    fun `ring is cut into twenty segments by default`() {
        assertEquals(16, OrbitDonutProgressDefaults.SegmentCount)
    }

    @Test
    fun `each of twenty segments is five percent of the ring`() {
        assertEquals(0, litSegments(0f, 20))
        assertEquals(1, litSegments(0.01f, 20))
        assertEquals(12, litSegments(0.62f, 20))
        assertEquals(16, litSegments(0.78f, 20))
        assertEquals(19, litSegments(0.99f, 20))
        assertEquals(20, litSegments(1f, 20))
    }

    @Test
    fun `progress arcs start at twelve o clock`() {
        assertEquals(-90f, OrbitDonutProgressDefaults.StartAngle)
    }
}
