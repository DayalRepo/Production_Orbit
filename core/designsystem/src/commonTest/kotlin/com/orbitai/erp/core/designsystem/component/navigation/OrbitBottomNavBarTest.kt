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
        assertEquals(listOf("Dashboard", "Projects", "Orbit", "Approvals", "Inbox"), items.map { it.label })
        assertTrue(items[2].emphasized)
        assertEquals(OrbitIconsName(items[1].icon), "Archive04")
        assertEquals(OrbitIconsName(items[3].icon), "ReceiptIndianRupee")
        assertEquals(OrbitIconsName(items[4].icon), "BubbleChat")
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
            assertEquals(OrbitIconsName(items.last().icon), "BubbleChat")
            assertTrue(items.all { it.label.isNotBlank() })
        }
        assertEquals(OrbitIconsName(OrbitContractorNavItems.Invoices.icon), "ReceiptIndianRupee")
        assertEquals(
            OrbitIconsName(OrbitProcurementManagerNavItems.Approvals.icon),
            "ReceiptIndianRupee",
        )
        assertEquals(OrbitIconsName(OrbitProcurementManagerNavItems.Orders.icon), "ShoppingCart")
        assertEquals(OrbitProjectManagerNavItems.Work.label, "Work")
        assertEquals(OrbitSiteEngineerNavItems.Work.label, "Work")
    }

    @Test
    fun `bottom nav metrics scale with width and keep glyphs under bar height`() {
        val sizing = OrbitSizing()
        val phone = orbitBottomNavMetrics(360.dp, sizing)
        val tablet = orbitBottomNavMetrics(700.dp, sizing)
        assertTrue(phone.aiGlyph >= phone.glyph)
        assertTrue(phone.glyph < phone.height)
        assertTrue(phone.glyph >= 24.dp)
        assertTrue(phone.glyph <= 30.dp)
        assertTrue(tablet.glyph >= phone.glyph)
        assertTrue(phone.iconStroke >= 1.10.dp)
        assertTrue(phone.iconStroke <= 1.30.dp)
    }
}

private fun OrbitIconsName(icon: androidx.compose.ui.graphics.vector.ImageVector): String = icon.name
