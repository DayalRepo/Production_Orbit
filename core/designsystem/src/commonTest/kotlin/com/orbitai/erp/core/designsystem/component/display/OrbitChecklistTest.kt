package com.orbitai.erp.core.designsystem.component.display

import kotlin.test.Test
import kotlin.test.assertEquals

class OrbitChecklistTest {

    @Test
    fun `progress label counts checked over total`() {
        val items = listOf(
            OrbitChecklistItem("1", "A", checked = true),
            OrbitChecklistItem("2", "B", checked = false),
            OrbitChecklistItem("3", "C", checked = true),
        )
        assertEquals("2 of 3 complete", orbitChecklistProgressLabel(items))
        assertEquals(0f, orbitChecklistProgress(emptyList()))
        assertEquals(2f / 3f, orbitChecklistProgress(items))
    }

    @Test
    fun `empty checklist reports zero of zero`() {
        assertEquals("0 of 0 complete", orbitChecklistProgressLabel(emptyList()))
    }

    @Test
    fun `next open item and remaining copy`() {
        val items = listOf(
            OrbitChecklistItem("1", "A", checked = true),
            OrbitChecklistItem("2", "B", checked = false),
            OrbitChecklistItem("3", "C", checked = false),
        )
        assertEquals("B", orbitChecklistNextOpen(items)?.label)
        assertEquals(2, orbitChecklistRemainingCount(items))
        assertEquals("2 left", orbitChecklistRemainingLabel(items))
        assertEquals("All complete", orbitChecklistRemainingLabel(items.map { it.copy(checked = true) }))
        assertEquals(null, orbitChecklistNextOpen(emptyList()))
    }

    @Test
    fun `create requires title and at least one item`() {
        assertEquals(false, orbitChecklistCanCreate("", emptyList()))
        assertEquals(false, orbitChecklistCanCreate("Title", emptyList()))
        assertEquals(false, orbitChecklistCanCreate("  ", listOf(OrbitChecklistItem("1", "A"))))
        assertEquals(
            true,
            orbitChecklistCanCreate(
                "Tower A",
                listOf(OrbitChecklistItem("1", "Inspect formwork")),
            ),
        )
    }
}
