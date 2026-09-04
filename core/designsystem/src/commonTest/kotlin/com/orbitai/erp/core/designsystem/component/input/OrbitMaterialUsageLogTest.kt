package com.orbitai.erp.core.designsystem.component.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrbitMaterialUsageLogTest {

    @Test
    fun `a line is complete only with material quantity and unit`() {
        val incomplete = OrbitMaterialUsageLine(id = "1", quantity = 4)
        assertFalse(orbitMaterialUsageLineComplete(incomplete))
        assertTrue(
            orbitMaterialUsageLineComplete(
                OrbitMaterialUsageLine(
                    id = "1",
                    material = "Cement (OPC 53)",
                    quantity = 4,
                    unit = "Bags",
                ),
            ),
        )
    }

    @Test
    fun `complete count ignores empty rows`() {
        val lines = listOf(
            OrbitMaterialUsageLine(id = "1", material = "Cement (OPC 53)", quantity = 2, unit = "Bags"),
            OrbitMaterialUsageLine(id = "2"),
        )
        assertEquals(1, orbitMaterialUsageCompleteCount(lines))
        assertEquals(0, orbitMaterialUsageCompleteCount(emptyList()))
    }

    @Test
    fun `suggested unit follows the material`() {
        assertEquals("Bags", orbitSuggestedUnitForMaterial("Cement (OPC 53)"))
        assertEquals("Kg", orbitSuggestedUnitForMaterial("Reinforcement Steel Fe500D"))
        assertEquals("Cubic Metres", orbitSuggestedUnitForMaterial("Ready-Mix Concrete M25"))
        assertEquals("Square Metres", orbitSuggestedUnitForMaterial("Vitrified Tiles 600x600"))
        assertEquals(null, orbitSuggestedUnitForMaterial("Unknown widget"))
    }
}
