package com.orbitai.erp.ui.card

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UnitWorkLogTest {

    @Test
    fun `villa task and villa-12 issue belong to villa 12 unit`() {
        val unit = sampleUnitVilla()
        assertTrue(sampleTaskVilla().belongsToUnit(unit))
        assertTrue(sampleIssueVilla12().belongsToUnit(unit))
        assertFalse(sampleIssueVilla().belongsToUnit(unit))
        assertFalse(sampleTaskApartment().belongsToUnit(unit))
        assertFalse(sampleOrderVilla().belongsToUnit(unit))
    }

    @Test
    fun `apartment task and a-101 issue belong to a-101 unit`() {
        val unit = sampleUnitApartment()
        assertTrue(sampleTaskApartment().belongsToUnit(unit))
        assertTrue(sampleIssueApartment101().belongsToUnit(unit))
        assertFalse(sampleIssueApartment().belongsToUnit(unit))
        assertFalse(sampleTaskVilla().belongsToUnit(unit))
    }

    @Test
    fun `forUnit splits tasks and issues`() {
        val unit = sampleUnitVilla()
        val log = unitWorkLogSamples()
        assertEquals(listOf("T-1042"), log.unitTasks(unit).map { it.number })
        assertEquals(listOf("I-2040"), log.unitIssues(unit).map { it.number })
    }

    @Test
    fun `unitUsedMaterials merges duplicate material and unit`() {
        val unit = sampleUnitVilla()
        val lines = unitWorkLogSamples().unitUsedMaterials(unit)
        val wire = lines.first { it.material.equals("Binding Wire", ignoreCase = true) }
        // Task villa uses 5kg + issue villa12 uses 3kg
        assertEquals(8, wire.quantity)
        assertEquals("Kg", wire.unit)
        assertTrue(lines.any { it.material.equals("OPC 53 Grade Cement", ignoreCase = true) })
        assertTrue(lines.any { it.material.equals("Plywood Shutters", ignoreCase = true) })
    }

    @Test
    fun `apartment location subtitle joins tower and floor`() {
        assertEquals("Tower A · 1st floor", sampleUnitApartment().locationSubtitle)
        assertEquals("1st floor", sampleUnitVilla().locationSubtitle)
    }
}
