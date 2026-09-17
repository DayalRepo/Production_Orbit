package com.orbitai.erp.core.designsystem.component.navigation

import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.theme.OrbitSizing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrbitBottomNavBarTest {

    @Test
    fun `CEO bar has five icon slots with Orbit AI in the center`() {
        val items = OrbitCeoNavItems.items
        assertEquals(5, items.size)
        assertEquals(listOf("Home", "Projects", "AI", "Invoice", "Chat"), items.map { it.label })
        assertTrue(items[2].emphasized)
        assertEquals(OrbitIconsName(items[1].icon), "Archive04Solid")
        assertEquals(OrbitIconsName(items[3].icon), "ReceiptIndianRupeeSolid")
        assertEquals(OrbitIconsName(items[4].icon), "BubbleChatSolid")
    }

    @Test
    fun `role nav slots match agreed work flows`() {
        assertEquals(
            listOf("Home", "Ongoing", "AI", "Plan", "Chat"),
            OrbitProjectManagerNavItems.items.map { it.label },
        )
        assertEquals(OrbitIconsName(OrbitProjectManagerNavItems.Ongoing.icon), "NotebookTextSolid")
        assertEquals(OrbitIconsName(OrbitProjectManagerNavItems.Plan.icon), "Calendar01Solid")

        assertEquals(
            listOf("Home", "Work", "AI", "Done", "Chat"),
            OrbitSiteEngineerNavItems.items.map { it.label },
        )
        assertEquals(OrbitIconsName(OrbitSiteEngineerNavItems.Done.icon), "CircleCheckSolid")

        assertEquals(
            listOf("Home", "Work", "AI", "Invoice", "Chat"),
            OrbitContractorNavItems.items.map { it.label },
        )
        assertEquals(OrbitIconsName(OrbitContractorNavItems.Invoices.icon), "ReceiptIndianRupeeSolid")

        assertEquals(
            listOf("Home", "Inspect", "AI", "Done", "Chat"),
            OrbitQaQcNavItems.items.map { it.label },
        )
        assertEquals(OrbitIconsName(OrbitQaQcNavItems.Done.icon), "CircleCheckSolid")
        assertEquals(OrbitIconsName(OrbitQaQcNavItems.Inspect.icon), "NotebookTextSolid")

        assertEquals(OrbitIconsName(OrbitProcurementManagerNavItems.Orders.icon), "ShoppingCartSolid")
        assertEquals(
            listOf("Home", "Orders", "AI", "Invoice", "Chat"),
            OrbitProcurementManagerNavItems.items.map { it.label },
        )
        assertEquals(
            listOf("Home", "Store", "AI", "Audit", "Chat"),
            OrbitWarehouseManagerNavItems.items.map { it.label },
        )
    }

    @Test
    fun `each role nav keeps five unique ids with center Orbit AI and Orbit inbox icon`() {
        listOf(
            OrbitCeoNavItems.items,
            OrbitProjectManagerNavItems.items,
            OrbitSiteEngineerNavItems.items,
            OrbitContractorNavItems.items,
            OrbitWarehouseManagerNavItems.items,
            OrbitProcurementManagerNavItems.items,
            OrbitQaQcNavItems.items,
        ).forEach { items ->
            assertEquals(5, items.size)
            assertEquals(items.map { it.id }.size, items.map { it.id }.toSet().size)
            assertTrue(items[2].emphasized)
            assertEquals(OrbitIconsName(items.last().icon), "BubbleChatSolid")
            assertTrue(items.all { it.label.isNotBlank() })
        }
    }

    @Test
    fun `bottom nav metrics scale with width and keep equal glyphs under active circle`() {
        val sizing = OrbitSizing()
        val phone = orbitBottomNavMetrics(360.dp, sizing)
        val tablet = orbitBottomNavMetrics(700.dp, sizing)
        assertTrue(phone.glyph < phone.activeCircle)
        assertTrue(phone.activeCircle <= phone.height)
        assertTrue(phone.glyph >= 22.dp)
        assertTrue(phone.glyph <= 28.dp)
        assertTrue(phone.activeCircle >= 56.dp)
        assertTrue(tablet.activeCircle >= phone.activeCircle)
        assertEquals(2.dp, phone.labelGap)
    }
}

private fun OrbitIconsName(icon: androidx.compose.ui.graphics.vector.ImageVector): String = icon.name
