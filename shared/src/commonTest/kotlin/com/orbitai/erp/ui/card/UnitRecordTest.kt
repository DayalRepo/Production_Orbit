package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofKind
import com.orbitai.erp.core.model.ProjectType
import kotlin.test.Test
import kotlin.test.assertEquals

class UnitRecordTest {

    @Test
    fun `villa card uses villa stage proof and view label`() {
        val unit = sampleUnitVilla()
        assertEquals(OrbitStageProofKind.Villa, unit.stageProofKind)
        assertEquals("View villa", unit.viewActionLabel)
        assertEquals(ProjectType.Villas, unit.projectType)
    }

    @Test
    fun `apartment card uses building stage proof and view unit label`() {
        val unit = sampleUnitApartment()
        assertEquals(OrbitStageProofKind.Building, unit.stageProofKind)
        assertEquals("View unit", unit.viewActionLabel)
        assertEquals(ProjectType.ApartmentCommunity, unit.projectType)
    }

    @Test
    fun `delay and issues labels read cleanly`() {
        assertEquals("No delay", sampleUnitVilla().delayLabel)
        assertEquals("1 issue", sampleUnitVilla().issuesLabel)

        assertEquals("5 months", sampleUnitApartment().delayLabel)
        assertEquals("1 issue", sampleUnitApartment().issuesLabel)

        assertEquals("1 month", unitMonthsLabel(1))
        assertEquals("2 issues", sampleUnitVilla().copy(issueCount = 2).issuesLabel)
    }
}
