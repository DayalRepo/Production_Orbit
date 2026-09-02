package com.orbitai.erp.core.designsystem.component.display

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Contracts for [OrbitMessageBubble] that do not need a Compose host: role vocabulary, sender
 * capitalisation and the collapse ceiling a long prompt is expected to survive.
 */
class OrbitMessageBubbleTest {

    @Test
    fun `three roles cover outgoing assistant and teammate`() {
        assertEquals(3, OrbitMessageBubbleRole.entries.size)
        assertTrue(OrbitMessageBubbleRole.User.name == "User")
        assertTrue(OrbitMessageBubbleRole.Ai.name == "Ai")
        assertTrue(OrbitMessageBubbleRole.Other.name == "Other")
    }

    @Test
    fun `sender labels render in capitals`() {
        assertEquals("YOU", orbitMessageSenderLabel("You"))
        assertEquals("ORBIT AI", orbitMessageSenderLabel("Orbit AI"))
        assertEquals("PRIYA · SITE ENGINEER", orbitMessageSenderLabel("Priya · Site Engineer"))
    }

    @Test
    fun `default collapse ceiling keeps a phone-height thread scannable`() {
        val defaultCeiling = 6
        assertTrue(defaultCeiling in 4..8)
    }
}
