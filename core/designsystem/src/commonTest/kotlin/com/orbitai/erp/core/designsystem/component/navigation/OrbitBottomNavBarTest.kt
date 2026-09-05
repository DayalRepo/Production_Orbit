package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrbitBottomNavBarTest {

    @Test
    fun `CEO primary pill has three destinations and a separate assistant action`() {
        assertEquals(3, OrbitCeoNavItems.primary.size)
        assertEquals(OrbitCeoNavIds.Dashboard, OrbitCeoNavItems.primary[0].id)
        assertEquals(OrbitCeoNavIds.Projects, OrbitCeoNavItems.primary[1].id)
        assertEquals(OrbitCeoNavIds.Messages, OrbitCeoNavItems.primary[2].id)
        assertEquals(OrbitCeoNavIds.Assistant, OrbitCeoNavItems.action.id)
    }

    @Test
    fun `each role nav keeps unique destination ids across pill and circle`() {
        val sets = listOf(
            OrbitCeoNavItems.primary + OrbitCeoNavItems.action,
            OrbitProjectManagerNavItems.primary + OrbitProjectManagerNavItems.action,
            OrbitSiteEngineerNavItems.primary + OrbitSiteEngineerNavItems.action,
            OrbitContractorNavItems.primary + OrbitContractorNavItems.action,
            OrbitWarehouseManagerNavItems.primary + OrbitWarehouseManagerNavItems.action,
            OrbitProcurementManagerNavItems.primary + OrbitProcurementManagerNavItems.action,
            OrbitQaQcNavItems.primary + OrbitQaQcNavItems.action,
        )
        sets.forEach { items ->
            val ids = items.map { it.id }
            assertEquals(ids.size, ids.toSet().size, "duplicate id in $ids")
            assertEquals(3, items.dropLast(1).size)
            assertTrue(items.all { it.contentDescription.isNotBlank() })
        }
    }

    @Test
    fun `role middle icons match the Hugeicons map`() {
        assertEquals(OrbitIconsName(OrbitProjectManagerNavItems.Tasks.icon), "NoteAdd")
        assertEquals(OrbitIconsName(OrbitSiteEngineerNavItems.Notes.icon), "NotepadText")
        assertEquals(OrbitIconsName(OrbitContractorNavItems.Notes.icon), "NotepadText")
        assertEquals(OrbitIconsName(OrbitWarehouseManagerNavItems.Inventory.icon), "Warehouse")
        assertEquals(OrbitIconsName(OrbitProcurementManagerNavItems.Orders.icon), "ShoppingCartAdd01")
        assertEquals(OrbitIconsName(OrbitQaQcNavItems.Inspections.icon), "BadgeCheck")
    }

    @Test
    fun `bottom nav metrics scale with available width`() {
        val sizing = OrbitSizing()
        val phone = orbitBottomNavMetrics(360.dp, sizing)
        val tablet = orbitBottomNavMetrics(700.dp, sizing)
        val desktop = orbitBottomNavMetrics(1280.dp, sizing)

        assertTrue(phone.height < tablet.height)
        assertTrue(tablet.height <= desktop.height)
        assertTrue(phone.glyph < tablet.glyph)
        assertTrue(phone.activeSize < tablet.activeSize)
        assertTrue(phone.activeSize <= phone.height - 8.dp)
        assertTrue(tablet.activeSize <= tablet.height - 8.dp)
        assertEquals(560.dp, tablet.barMaxWidth)
        assertEquals(640.dp, desktop.barMaxWidth)
        // Edge inset is platform chrome, not width-scaled — keeps tab/nav columns aligned.
        assertEquals(sizing.bottomNavEdgeInset, phone.edgeInset)
        assertEquals(sizing.bottomNavEdgeInset, tablet.edgeInset)
        assertEquals(sizing.tabBarEdgeInset, phone.edgeInset)
    }
}

/** ImageVector.name is stable for our generated set and cheap to assert in host tests. */
private fun OrbitIconsName(icon: androidx.compose.ui.graphics.vector.ImageVector): String = icon.name
