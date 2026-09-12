package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitCopyButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Who sent the bubble. [User] aligns end (right); [Ai] / [Other] align start (left).
 */
enum class OrbitMessageBubbleRole {
    User,
    Ai,
    Other,
}

/** One message under a parent in [OrbitMessageThread]. */
@Immutable
data class OrbitMessageReply(
    val text: String,
    val role: OrbitMessageBubbleRole = OrbitMessageBubbleRole.User,
    val senderLabel: String? = null,
    val timestamp: String? = null,
)

/**
 * Chat / AI message: plain text (no glass card). Sender, body, optional show more/less,
 * timestamp + small copy control.
 */
@Composable
fun OrbitMessageBubble(
    text: String,
    role: OrbitMessageBubbleRole,
    modifier: Modifier = Modifier,
    senderLabel: String? = null,
    timestamp: String? = null,
    collapsedMaxLines: Int = 4,
) {
    val isUser = role == OrbitMessageBubbleRole.User
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        MessageBubbleCard(
            text = text,
            role = role,
            senderLabel = senderLabel,
            timestamp = timestamp,
            collapsedMaxLines = collapsedMaxLines,
            contentAlign = if (isUser) Alignment.End else Alignment.Start,
            textAlign = if (isUser) TextAlign.End else TextAlign.Start,
        )
    }
}

/** Parent message plus zero or more replies. */
@Composable
fun OrbitMessageThread(
    text: String,
    role: OrbitMessageBubbleRole,
    replies: List<OrbitMessageReply>,
    modifier: Modifier = Modifier,
    senderLabel: String? = null,
    timestamp: String? = null,
    collapsedMaxLines: Int = 4,
) {
    val spacing = OrbitTheme.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        OrbitMessageBubble(
            text = text,
            role = role,
            senderLabel = senderLabel,
            timestamp = timestamp,
            collapsedMaxLines = collapsedMaxLines,
        )
        replies.forEach { reply ->
            OrbitMessageBubble(
                text = reply.text,
                role = reply.role,
                senderLabel = reply.senderLabel,
                timestamp = reply.timestamp,
                collapsedMaxLines = collapsedMaxLines,
            )
        }
    }
}

/** Normalises sender chrome to `YOU`, `ORBIT AI`, `PRIYA · SITE ENGINEER`. */
fun orbitMessageSenderLabel(raw: String): String = raw.trim().uppercase()

@Composable
private fun MessageBubbleCard(
    text: String,
    role: OrbitMessageBubbleRole,
    contentAlign: Alignment.Horizontal,
    textAlign: TextAlign,
    senderLabel: String? = null,
    timestamp: String? = null,
    collapsedMaxLines: Int = 4,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val dark = OrbitTheme.isDark
    val linkInk = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50
    val isUser = role == OrbitMessageBubbleRole.User

    var expanded by remember(text) { mutableStateOf(false) }
    var overflows by remember(text, collapsedMaxLines) { mutableStateOf(false) }

    val displaySender = senderLabel?.let { orbitMessageSenderLabel(it) }
    val spoken = buildString {
        displaySender?.let { append(it); append(". ") }
        append(role.name)
        append(". ")
        append(text)
    }

    Column(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .wrapContentWidth(align = contentAlign)
            .padding(horizontal = spacing.xxs, vertical = spacing.xs)
            .semantics { contentDescription = spoken },
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
        horizontalAlignment = contentAlign,
    ) {
        if (displaySender != null) {
            Text(
                text = displaySender,
                style = OrbitTheme.typography.labelSmall,
                color = content.textTertiary,
                fontWeight = OrbitTheme.fontWeights.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = textAlign,
            )
        }

        Text(
            text = text,
            style = OrbitTheme.extendedTypography.bodyLongForm.copy(fontWeight = OrbitTheme.fontWeights.title),
            color = content.textPrimary,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            onTextLayout = { layout ->
                if (!expanded) {
                    overflows = layout.hasVisualOverflow
                }
            },
        )

        if (overflows || expanded) {
            Text(
                text = if (expanded) "Show less" else "Show more",
                style = OrbitTheme.typography.labelMedium.copy(fontWeight = OrbitTheme.fontWeights.heading),
                color = linkInk,
                modifier = Modifier
                    .orbitHandCursor()
                    .clickable(role = Role.Button) { expanded = !expanded }
                    .semantics {
                        contentDescription = if (expanded) "Show less message" else "Show more message"
                    },
            )
        }

        if (timestamp != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    spacing.xxs,
                    if (isUser) Alignment.End else Alignment.Start,
                ),
            ) {
                Text(
                    text = timestamp,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textTertiary,
                    maxLines = 1,
                    textAlign = textAlign,
                )
                OrbitCopyButton(
                    value = text,
                    label = "Message",
                    size = OrbitIconButtonSize.Small,
                )
            }
        }
    }
}
