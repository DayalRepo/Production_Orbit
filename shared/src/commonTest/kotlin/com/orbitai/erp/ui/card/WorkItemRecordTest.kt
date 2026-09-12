package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofKind
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.ui.component.dropdown.WorkStage
import kotlin.test.Test
import kotlin.test.assertEquals

class WorkItemRecordTest {

    @Test
    fun `villa location is villa and floor`() {
        val record = sampleTaskVilla().copy(
            villa = "Villa 12",
            floor = "First",
        )
        assertEquals("Villa 12 · First", record.locationLine)
    }

    @Test
    fun `apartment location is tower floor and unit`() {
        val record = sampleTaskApartment().copy(
            tower = "Tower A",
            floor = "First",
            apartmentUnit = "A-101",
        )
        assertEquals("Tower A · First · A-101", record.locationLine)
    }

    @Test
    fun `stage heading and task title are separate`() {
        val record = sampleTaskVilla().copy(
            stage = WorkStage.Structure,
            task = "Slab Concreting",
            projectType = ProjectType.Villas,
            status = WorkStatus.Open,
        )
        assertEquals("Structure", record.stageHeading)
        assertEquals("Slab Concreting", record.taskTitle)
        assertEquals("T-1042", record.number)
    }

    @Test
    fun `villa stage proof uses villa sequence`() {
        val record = sampleTaskVilla()
        assertEquals(OrbitStageProofKind.Villa, record.stageProofKind)
        assertEquals(0, record.stageProofCompletedCount)
    }

    @Test
    fun `apartment stage proof uses building sequence`() {
        val record = sampleTaskApartment()
        assertEquals(OrbitStageProofKind.Building, record.stageProofKind)
        assertEquals(2, record.stageProofCompletedCount)
    }
}
