package com.orbitai.erp.ui.component.dropdown

import com.orbitai.erp.core.model.ProjectType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WorkSequenceTest {

    @Test
    fun `villa omits common area and basement`() {
        val stages = WorkSequence.stagesFor(ProjectType.Villas)
        assertEquals(
            listOf(
                WorkStage.Structure,
                WorkStage.UnitInternal,
                WorkStage.UnitExternal,
                WorkStage.ExternalDevelopment,
            ),
            stages,
        )
        assertTrue(WorkSequence.tasksFor(ProjectType.Villas, WorkStage.CommonArea).isEmpty())
        assertTrue(WorkSequence.tasksFor(ProjectType.Villas, WorkStage.Basement).isEmpty())
    }

    @Test
    fun `apartment includes common area and basement`() {
        val stages = WorkSequence.stagesFor(ProjectType.ApartmentCommunity)
        assertEquals(
            listOf(
                WorkStage.Structure,
                WorkStage.CommonArea,
                WorkStage.UnitInternal,
                WorkStage.UnitExternal,
                WorkStage.ExternalDevelopment,
                WorkStage.Basement,
            ),
            stages,
        )
        assertTrue(WorkSequence.tasksFor(ProjectType.ApartmentCommunity, WorkStage.CommonArea).isNotEmpty())
        assertTrue(WorkSequence.tasksFor(ProjectType.ApartmentCommunity, WorkStage.Basement).isNotEmpty())
    }

    @Test
    fun `villa internals keep the pdf qualifiers and omit fire sprinkler`() {
        val tasks = WorkSequence.tasksFor(ProjectType.Villas, WorkStage.UnitInternal)
        assertTrue("PHE Works" in tasks)
        assertTrue("Toilet Tile Grouting (if applicable)" in tasks)
        assertTrue(tasks.none { "Fire Sprinkler" in it })
    }
}
