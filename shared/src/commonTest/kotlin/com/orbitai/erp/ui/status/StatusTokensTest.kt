package com.orbitai.erp.ui.status

import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeEmphasis
import com.orbitai.erp.core.model.Priority
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.StockLevel
import com.orbitai.erp.core.model.WorkStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StatusTokensTest {

    @Test
    fun `severity levels ascend and stay within the segment count`() {
        val levels = Severity.entries.map { it.indicatorLevel }
        assertEquals(levels.sorted(), levels, "Severity order must match indicator order")
        assertEquals(Severity.entries.size, levels.distinct().size, "Levels must be distinct")
        assertTrue(levels.all { it in 1..4 }, "Levels must fit the four-segment indicator")
    }

    @Test
    fun `priority levels ascend and stay within the segment count`() {
        val levels = Priority.entries.map { it.indicatorLevel }
        assertEquals(levels.sorted(), levels, "Priority order must match indicator order")
        assertEquals(Priority.entries.size, levels.distinct().size, "Levels must be distinct")
        assertTrue(levels.all { it in 1..4 }, "Levels must fit the four-segment indicator")
    }

    @Test
    fun `only blocking states get solid emphasis`() {
        // Solid emphasis is loud by design, so it stays rationed to states that halt site work.
        val loudStatuses = WorkStatus.entries.filter { it.emphasis == OrbitBadgeEmphasis.Solid }
        assertEquals(listOf(WorkStatus.Blocked), loudStatuses)

        val loudStock = StockLevel.entries.filter { it.emphasis == OrbitBadgeEmphasis.Solid }
        assertEquals(listOf(StockLevel.OutOfStock), loudStock)
    }
}
