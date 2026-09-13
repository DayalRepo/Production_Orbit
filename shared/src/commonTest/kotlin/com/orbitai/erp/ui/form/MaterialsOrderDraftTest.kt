package com.orbitai.erp.ui.form

import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.ui.datetime.orbitToday
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MaterialsOrderDraftTest {

    @Test
    fun `confirm needs a complete line a date and an assignee`() {
        val draft = MaterialsOrderDraft()
        assertFalse(draft.isReadyToConfirm())
        draft.materialLines[0] = OrbitMaterialUsageLine("m0", "Cement Putty", 6, "Kg")
        assertFalse(draft.isReadyToConfirm())
        draft.projectName = "Prestige Golfshire Villas"
        assertFalse(draft.isReadyToConfirm())
        draft.dateRange = OrbitDateRange(orbitToday(), orbitToday())
        assertFalse(draft.isReadyToConfirm())
        draft.procurementIds = setOf("u-proc")
        assertTrue(draft.isReadyToConfirm())
        assertEquals(1, draft.completeMaterialLines().size)
        draft.materialLines += OrbitMaterialUsageLine("m1", "Binding Wire", 8, "Kg")
        assertEquals(listOf("Cement Putty"), draft.completeMaterialLines().map { it.material })
    }
}
