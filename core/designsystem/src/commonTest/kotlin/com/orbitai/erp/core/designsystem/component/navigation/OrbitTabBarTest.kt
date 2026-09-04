package com.orbitai.erp.core.designsystem.component.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class OrbitTabBarTest {

    @Test
    fun `tab identity is the selection key`() {
        val tabs = listOf(
            OrbitTab(id = "comments", label = "Comments"),
            OrbitTab(id = "updates", label = "Updates"),
        )
        assertEquals("Comments", tabs.first { it.id == "comments" }.label)
        assertEquals(2, tabs.size)
    }
}
