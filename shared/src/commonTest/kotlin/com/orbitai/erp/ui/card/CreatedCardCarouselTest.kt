package com.orbitai.erp.ui.card

import com.orbitai.erp.core.model.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreatedCardCarouselTest {

    @Test
    fun `page label is one-based over the total`() {
        assertEquals("0/0", createdCardPageLabel(0, 0))
        assertEquals("1/4", createdCardPageLabel(0, 4))
        assertEquals("2/4", createdCardPageLabel(1, 4))
        assertEquals("4/4", createdCardPageLabel(3, 4))
        assertEquals("4/4", createdCardPageLabel(9, 4))
    }

    @Test
    fun `slides are one card per role per item`() {
        val slides = createdCardSlides(listOf(sampleTaskVilla(), sampleIssueVilla()))
        assertEquals(WorkItemCardRoles.size * 2, slides.size)
        assertEquals(UserRole.ProjectManager, slides.first().role)
        assertEquals(UserRole.QaQc, slides[WorkItemCardRoles.lastIndex].role)
        assertEquals("T-1042", slides.first().record.number)
        assertEquals("I-2031", slides.last().record.number)
        assertEquals(true, workItemIsReviewer(UserRole.ProjectManager))
        assertEquals(true, workItemIsReviewer(UserRole.QaQc))
        assertEquals(false, workItemIsReviewer(UserRole.SiteEngineer))
    }

    @Test
    fun `purchase order slides are one card per supply role`() {
        val slides = createdCardSlides(listOf(sampleOrderVilla()))
        assertEquals(PurchaseOrderCardRoles, slides.map { it.role })
        assertEquals("PO-1234", slides.first().record.number)
        assertEquals(UserRole.ProcurementManager, slides[1].role)
        assertEquals(UserRole.WarehouseManager, slides.last().role)
    }

    @Test
    fun `a short horizontal drag still pages`() {
        assertEquals(1, createdCardSwipeTargetPage(current = 0, lastIndex = 3, dragPx = -80f, pageWidthPx = 400f))
        assertEquals(0, createdCardSwipeTargetPage(current = 1, lastIndex = 3, dragPx = 80f, pageWidthPx = 400f))
        assertEquals(1, createdCardSwipeTargetPage(current = 1, lastIndex = 3, dragPx = -20f, pageWidthPx = 400f))
        assertEquals(0, createdCardSwipeTargetPage(current = 0, lastIndex = 3, dragPx = -80f, pageWidthPx = 0f))
    }

    @Test
    fun `dot window stays on the current page and never peeks past the ends`() {
        assertEquals(0 until 4, createdCardDotWindow(current = 1, count = 4))
        val window = createdCardDotWindow(current = 8, count = 10, maxDots = 7)
        assertEquals(7, window.count())
        assertTrue(8 in window)
        assertEquals(3 until 10, window)
    }
}
