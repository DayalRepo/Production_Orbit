package com.orbitai.erp.ui.form.page

import kotlin.test.Test
import kotlin.test.assertEquals

class IssueImageNamesTest {

    @Test
    fun `first photo is issueimage1 with the source extension`() {
        assertEquals("issueimage1.png", nextIssueImageName(emptyList(), "IMG_2048.png"))
        assertEquals("issueimage1.jpg", nextIssueImageName(emptyList(), "capture.jpg"))
    }

    @Test
    fun `unknown extension defaults to jpg`() {
        assertEquals("issueimage1.jpg", nextIssueImageName(emptyList(), "photo"))
        assertEquals("issueimage1.jpg", nextIssueImageName(emptyList(), "note.pdf"))
    }

    @Test
    fun `index advances past names already on the form`() {
        assertEquals(
            "issueimage3.jpg",
            nextIssueImageName(listOf("issueimage1.png", "issueimage2.jpg"), "snap.jpeg"),
        )
    }
}
