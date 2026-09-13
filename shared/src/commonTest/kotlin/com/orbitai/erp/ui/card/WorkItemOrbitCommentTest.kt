package com.orbitai.erp.ui.card

import com.orbitai.erp.ui.component.composer.mentionDraftOf
import com.orbitai.erp.ui.component.composer.replaceMentionDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkItemOrbitCommentTest {

    @Test
    fun `at opens a mention draft until a known token is complete`() {
        val tokens = WorkItemMention.entries.map { it.token }.toSet()
        assertEquals("", mentionDraftOf("@", tokens))
        assertEquals("Ra", mentionDraftOf("Need help @Ra", tokens))
        assertNull(mentionDraftOf("@RaiseIssue more", tokens))
        assertEquals("Need help @RaiseIssue ", replaceMentionDraft("Need help @Ra", "RaiseIssue"))
    }

    @Test
    fun `mentions are parsed from the sent comment`() {
        val mentions = parseWorkItemMentions("@RaiseIssue crack at reveal and @Material cement")
        assertEquals(
            setOf(WorkItemMention.RaiseIssue, WorkItemMention.Material),
            mentions,
        )
        assertEquals("cement for the pour", materialNameFromMention("@Material cement for the pour"))
    }

    @Test
    fun `orbit ai raises an issue and logs a missing material`() {
        val next = sampleTaskVilla().applyOrbitComment(
            "@RaiseIssue hairline crack  @Material Cement Putty",
        )
        assertEquals(1, next.raisedIssues.size)
        assertTrue(next.raisedIssues.first().startsWith("I-"))
        assertEquals("Cement Putty", next.usedMaterials.last().material)
        assertEquals("You", next.comments[next.comments.lastIndex - 1].author)
        assertTrue(next.comments.last().fromAi)
        assertTrue(next.comments.last().body.contains(next.raisedIssues.first()))
    }
}
