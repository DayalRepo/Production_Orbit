package com.orbitai.erp.core.designsystem.component.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrbitAssignFieldTest {

    private val roster = listOf(
        OrbitAssignMember("1", "Priya Sharma", "Site Engineer", "+91 98200 41122", "priya.sharma"),
        OrbitAssignMember("2", "Sanjay Iyer", "Contractor", "+91 90030 55817", "sanjay.iyer"),
        OrbitAssignMember("3", "Ravi Menon", "SE", "+91 99400 77310"),
    )

    @Test
    fun `display name uppercases without role`() {
        assertEquals("PRIYA SHARMA", orbitAssignDisplayName("Priya Sharma"))
        assertEquals("SANJAY IYER", orbitAssignDisplayName("Sanjay Iyer"))
    }

    @Test
    fun `search matches name role mobile and username`() {
        assertEquals(listOf(roster[0]), roster.filterAssignByQuery("priya"))
        assertEquals(listOf(roster[1]), roster.filterAssignByQuery("contractor"))
        assertEquals(listOf(roster[0]), roster.filterAssignByQuery("98200"))
        assertEquals(listOf(roster[1]), roster.filterAssignByQuery("sanjay.iyer"))
        assertTrue(roster.filterAssignByQuery("zzzz").isEmpty())
    }

    @Test
    fun `blank query returns full roster`() {
        assertEquals(roster, roster.filterAssignByQuery(""))
        assertEquals(roster, roster.filterAssignByQuery("   "))
    }

    @Test
    fun `monogram uses word initials`() {
        assertEquals("PS", roster[0].monogram)
        assertEquals("SI", roster[1].monogram)
        assertEquals("RM", roster[2].monogram)
    }

    @Test
    fun `selected members sort to top of list`() {
        val sorted = roster.sortAssignSelectedFirst(setOf("3", "1"))
        assertEquals(listOf("1", "3", "2"), sorted.map { it.id })
    }

    @Test
    fun `sort with no selection returns original order`() {
        assertEquals(roster, roster.sortAssignSelectedFirst(emptySet()))
    }
}
