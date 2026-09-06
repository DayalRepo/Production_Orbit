package com.orbitai.erp.core.model

import kotlin.test.Test
import kotlin.test.assertEquals

class UserRoleTest {

    @Test
    fun shortLabelsAreCapitalAbbreviations() {
        assertEquals("CEO", UserRole.Ceo.shortLabel)
        assertEquals("PM", UserRole.ProjectManager.shortLabel)
        assertEquals("SE", UserRole.SiteEngineer.shortLabel)
        assertEquals("CONTR", UserRole.Contractor.shortLabel)
        assertEquals("QA/QC", UserRole.QaQc.shortLabel)
        assertEquals("WM", UserRole.WarehouseManager.shortLabel)
        assertEquals("PROC", UserRole.ProcurementManager.shortLabel)
    }
}
