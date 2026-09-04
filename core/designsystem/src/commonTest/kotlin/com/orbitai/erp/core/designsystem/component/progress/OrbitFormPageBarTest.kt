package com.orbitai.erp.core.designsystem.component.progress

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrbitFormPageBarTest {

    @Test
    fun `page count drives how many bars the form shows`() {
        assertEquals(2, formPageBarCount(pageCount = 2))
        assertEquals(5, formPageBarCount(pageCount = 5))
        assertEquals(1, formPageBarCount(pageCount = 0))
    }

    @Test
    fun `current page is clamped into the form range`() {
        assertEquals(0, formPageCurrent(pageCount = 3, currentPage = -1))
        assertEquals(2, formPageCurrent(pageCount = 3, currentPage = 9))
        assertEquals(1, formPageCurrent(pageCount = 3, currentPage = 1))
    }

    @Test
    fun `bars through the current page are considered filled`() {
        assertTrue(formPageFilled(index = 0, currentPage = 2))
        assertTrue(formPageFilled(index = 2, currentPage = 2))
        assertTrue(!formPageFilled(index = 3, currentPage = 2))
    }
}

internal fun formPageBarCount(pageCount: Int): Int = pageCount.coerceAtLeast(1)

internal fun formPageCurrent(pageCount: Int, currentPage: Int): Int {
    val pages = formPageBarCount(pageCount)
    return currentPage.coerceIn(0, pages - 1)
}

internal fun formPageFilled(index: Int, currentPage: Int): Boolean = index <= currentPage
