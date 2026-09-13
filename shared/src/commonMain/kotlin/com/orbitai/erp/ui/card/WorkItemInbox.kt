package com.orbitai.erp.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.display.OrbitCountBadge
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageBubble
import com.orbitai.erp.core.designsystem.component.display.OrbitMessageBubbleRole
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.composer.ComposerMention
import com.orbitai.erp.ui.component.composer.MessageComposer

@Composable
internal fun WorkItemInboxEntry(
    unread: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val content = OrbitTheme.contentColors
    val spacing = OrbitTheme.spacing
    val ink = if (selected) content.textPrimary else content.textSecondary
    Row(
        modifier = modifier
            .clickable(role = Role.Button, onClick = onClick)
            .padding(start = spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Text(
            text = "Inbox",
            style = OrbitTheme.extendedTypography.sectionLabel,
            color = ink,
        )
        WorkItemInboxMark(unread = unread)
    }
}

@Composable
internal fun WorkItemInboxMark(
    unread: Int,
    modifier: Modifier = Modifier,
) {
    val sizing = OrbitTheme.sizing
    Box(modifier = modifier) {
        OrbitGlyph(
            icon = OrbitIcons.BubbleChat,
            size = sizing.iconMd,
            tint = OrbitTheme.contentColors.iconPrimary,
            contentDescription = if (unread > 0) {
                "$unread unread messages"
            } else {
                "Inbox"
            },
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 6.dp, y = (-4).dp),
        ) {
            OrbitCountBadge(count = unread, label = "unread messages")
        }
    }
}

@Composable
internal fun WorkItemInboxPane(
    record: WorkItemRecord,
    onRecordChange: (WorkItemRecord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val mentions = remember {
        WorkItemMention.entries.map { mention ->
            ComposerMention(mention.token, mention.label, OrbitIcons.BubbleChat)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.screenHorizontal),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            if (record.comments.isEmpty()) {
                EmptyReviewNote("No messages yet")
            } else {
                record.comments.forEach { comment ->
                    OrbitMessageBubble(
                        text = comment.body,
                        role = comment.bubbleRole(),
                        senderLabel = comment.author,
                        timestamp = comment.time,
                    )
                }
            }
        }
        MessageComposer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.sm),
            mentions = mentions,
            onSend = { text, _, _ ->
                if (text.isNotBlank()) {
                    onRecordChange(record.applyOrbitComment(text))
                }
            },
        )
    }
}

internal fun WorkItemComment.bubbleRole(): OrbitMessageBubbleRole = when {
    mine -> OrbitMessageBubbleRole.User
    fromAi -> OrbitMessageBubbleRole.Ai
    else -> OrbitMessageBubbleRole.Other
}
