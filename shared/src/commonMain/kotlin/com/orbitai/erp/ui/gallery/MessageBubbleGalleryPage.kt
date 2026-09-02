package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageBubble
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageBubbleRole
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageReply
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageThread
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

@Composable
internal fun MessageBubbleGalleryPage() {
    val spacing = OrbitTheme.spacing

    GallerySection("Message thread") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
        ) {
            OrbitMessageThread(
                text = "Pump still booked for Tower A Thursday — can we shift the level 4 pour?",
                role = OrbitMessageBubbleRole.Ai,
                senderLabel = "Orbit AI",
                timestamp = "09:15",
                replies = listOf(
                    OrbitMessageReply(
                        text = "Yes — move level 4 to Friday and hold the crane.",
                        role = OrbitMessageBubbleRole.User,
                        senderLabel = "You",
                        timestamp = "09:16",
                    ),
                    OrbitMessageReply(
                        text = "Noted. I'll hold the crane until you confirm.",
                        role = OrbitMessageBubbleRole.Other,
                        senderLabel = "Priya · Site Engineer",
                        timestamp = "09:18",
                    ),
                ),
            )
        }
    }

    GallerySection("Single bubble · long body") {
        OrbitMessageBubble(
            text = LongAiMessage,
            role = OrbitMessageBubbleRole.Ai,
            senderLabel = "Orbit AI",
            timestamp = "09:20",
        )
    }
}

private val LongAiMessage = """
    Resequencing keeps the Thursday pour if the pump clears Tower A by 11:00. Move the level 4 slab
    to Friday morning and bring the formwork crew forward one half-day. Hold the crane until Priya
    confirms the revised sequence on site.

    Tower B is still on the original window — only shift if the pump cannot return before noon.
    If we slip past 11:00, notify the batching plant and reschedule the level 4 inspection for
    Monday so the approval stage stays clean.

    I can draft the updated checklist and message the contractor once you confirm the Friday pour.
""".trimIndent()
