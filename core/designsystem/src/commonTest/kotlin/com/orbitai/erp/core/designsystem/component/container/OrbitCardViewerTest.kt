package com.orbitai.erp.core.designsystem.component.container

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrbitCardViewerTest {

    @Test
    fun `index labels are zero-padded from one`() {
        assertEquals("01", orbitCardIndexLabel(0))
        assertEquals("02", orbitCardIndexLabel(1))
        assertEquals("05", orbitCardIndexLabel(4))
        assertEquals("10", orbitCardIndexLabel(9))
    }

    @Test
    fun `stack puts the first card at the bottom and in front`() {
        val slots = (0 until 5).map { index ->
            orbitCardStackSlot(
                index = index,
                count = 5,
                viewportWidth = 400f,
                cardWidth = 320f,
                peek = 40f,
            )
        }
        assertTrue(slots[0].y > slots[1].y, "card 01 should sit below card 02")
        assertTrue(slots[1].y > slots[2].y)
        assertEquals(160f, slots[0].y)
        assertEquals(0f, slots[4].y)
        assertTrue(slots[0].zIndex > slots[1].zIndex, "card 01 should be in front of card 02")
        assertTrue(slots[1].zIndex > slots[4].zIndex)
        slots.forEach { slot ->
            assertEquals(40f, slot.x, "stack cards stay horizontally centred")
        }
    }

    @Test
    fun `carousel centres the selected card and peeks neighbours`() {
        val selected = orbitCardCarouselSlot(
            index = 2,
            pagerOffset = 2f,
            count = 5,
            viewportWidth = 400f,
            cardWidth = 300f,
            gap = 20f,
        )
        val left = orbitCardCarouselSlot(
            index = 1,
            pagerOffset = 2f,
            count = 5,
            viewportWidth = 400f,
            cardWidth = 300f,
            gap = 20f,
        )
        val right = orbitCardCarouselSlot(
            index = 3,
            pagerOffset = 2f,
            count = 5,
            viewportWidth = 400f,
            cardWidth = 300f,
            gap = 20f,
        )
        assertEquals(50f, selected.x)
        assertEquals(50f - 320f, left.x)
        assertEquals(50f + 320f, right.x)
        assertTrue(selected.zIndex > left.zIndex)
        assertTrue(selected.zIndex > right.zIndex)
    }

    @Test
    fun `stack height is the front card plus a peek per card behind it`() {
        assertEquals(0f, orbitCardStackHeight(cardHeight = 200f, count = 0, peek = 40f))
        assertEquals(200f, orbitCardStackHeight(cardHeight = 200f, count = 1, peek = 40f))
        assertEquals(320f, orbitCardStackHeight(cardHeight = 200f, count = 4, peek = 40f))
    }

    @Test
    fun `selected index stays inside the list`() {
        assertEquals(0, orbitCardViewerClampIndex(-2, count = 4))
        assertEquals(0, orbitCardViewerClampIndex(0, count = 4))
        assertEquals(3, orbitCardViewerClampIndex(3, count = 4))
        assertEquals(3, orbitCardViewerClampIndex(99, count = 4))
        assertEquals(0, orbitCardViewerClampIndex(2, count = 0))
    }

    @Test
    fun `lerp holds the endpoints and moves in a straight line`() {
        val from = OrbitCardSlot(x = 0f, y = 0f, zIndex = 1f)
        val to = OrbitCardSlot(x = 100f, y = 40f, zIndex = 5f)
        val start = lerpOrbitCardSlot(from, to, 0f)
        val mid = lerpOrbitCardSlot(from, to, 0.5f)
        val end = lerpOrbitCardSlot(from, to, 1f)
        assertEquals(from, start)
        assertEquals(to, end)
        assertEquals(50f, mid.x)
        assertEquals(20f, mid.y)
        assertEquals(3f, mid.zIndex)
    }
}
