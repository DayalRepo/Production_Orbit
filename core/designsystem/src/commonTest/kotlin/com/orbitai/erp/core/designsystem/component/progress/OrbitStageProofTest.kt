package com.orbitai.erp.core.designsystem.component.progress

import kotlin.test.Test
import kotlin.test.assertEquals

class OrbitStageProofTest {

    @Test
    fun `villa has four stages in work-sequence order`() {
        val stages = orbitStageProofVilla()
        assertEquals(4, stages.size)
        assertEquals(listOf("SR", "UI", "UE", "ED"), stages.map { it.code })
        assertEquals("Ext. development", stages.last().label)
    }

    @Test
    fun `building has six stages including common area and basement`() {
        val stages = orbitStageProofBuilding()
        assertEquals(6, stages.size)
        assertEquals(listOf("SR", "CA", "UI", "UE", "ED", "BS"), stages.map { it.code })
    }

    @Test
    fun `progress summary uses stage count`() {
        assertEquals("0/4 stages completed", orbitStageProofProgressSummary(0, 4))
        assertEquals("2/6 stages completed", orbitStageProofProgressSummary(2, 6))
        assertEquals("1/1 stage completed", orbitStageProofProgressSummary(1, 1))
    }

    @Test
    fun `phase maps completed count to done current and upcoming`() {
        assertEquals(OrbitStepPhase.Current, orbitStageProofPhase(0, completedCount = 0, total = 4))
        assertEquals(OrbitStepPhase.Completed, orbitStageProofPhase(0, completedCount = 1, total = 4))
        assertEquals(OrbitStepPhase.Current, orbitStageProofPhase(1, completedCount = 1, total = 4))
        assertEquals(OrbitStepPhase.Upcoming, orbitStageProofPhase(2, completedCount = 1, total = 4))
        assertEquals(OrbitStepPhase.Completed, orbitStageProofPhase(3, completedCount = 4, total = 4))
    }

    @Test
    fun `total days spans earliest start to latest end`() {
        val stages = listOf(
            OrbitStageProofStep("SR", "Structure", "01/08/2026", "10/08/2026"),
            OrbitStageProofStep("UI", "Unit internal", "11/08/2026", "20/08/2026"),
        )
        assertEquals(20, orbitStageProofTotalDays(stages))
        assertEquals("20 days", orbitStageProofTotalDaysLabel(stages))
    }
}
