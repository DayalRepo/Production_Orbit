package com.orbitai.erp.core.designsystem.component.media

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrbitImageCarouselTest {

    @Test
    fun `page labels start at one and clamp to the last page`() {
        assertEquals("0/0", orbitImageCarouselPageLabel(0, 0))
        assertEquals("1/4", orbitImageCarouselPageLabel(0, 4))
        assertEquals("2/4", orbitImageCarouselPageLabel(1, 4))
        assertEquals("4/4", orbitImageCarouselPageLabel(3, 4))
        assertEquals("4/4", orbitImageCarouselPageLabel(9, 4))
    }

    @Test
    fun `zoom steps stay between one and four`() {
        assertEquals(1f, orbitImageZoomBy(1f, -0.5f))
        assertEquals(1.5f, orbitImageZoomBy(1f, 0.5f))
        assertEquals(4f, orbitImageZoomBy(3.8f, 0.5f))
        assertEquals(4f, orbitImageZoomBy(4f, 0.5f))
    }

    @Test
    fun `image file names include common photo extensions`() {
        assertTrue(orbitIsImageFileName("slab1.jpg"))
        assertTrue(orbitIsImageFileName("SITE.PNG"))
        assertTrue(orbitIsImageFileName("note.heic"))
        assertFalse(orbitIsImageFileName("spec.pdf"))
        assertFalse(orbitIsImageFileName("no-extension"))
    }
}
