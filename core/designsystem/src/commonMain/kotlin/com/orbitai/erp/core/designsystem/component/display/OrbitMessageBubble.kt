package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Who sent the bubble. Every bubble is start-aligned for a single left column in threads.
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
 * A chat / AI message bubble: sender label, body and expand. Always start-aligned.
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
    MessageBubbleCard(
        text = text,
        role = role,
        modifier = modifier,
        senderLabel = senderLabel,
        timestamp = timestamp,
        collapsedMaxLines = collapsedMaxLines,
    )
}

/** Parent message plus zero or more replies, all start-aligned. */
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
        horizontalAlignment = Alignment.Start,
    ) {
        MessageBubbleCard(
            text = text,
            role = role,
            senderLabel = senderLabel,
            timestamp = timestamp,
            collapsedMaxLines = collapsedMaxLines,
            modifier = Modifier.fillMaxWidth(MaxBubbleFillFraction),
        )
        replies.forEach { reply ->
            MessageBubbleCard(
                text = reply.text,
                role = reply.role,
                senderLabel = reply.senderLabel,
                timestamp = reply.timestamp,
                collapsedMaxLines = collapsedMaxLines,
                modifier = Modifier.fillMaxWidth(MaxBubbleFillFraction),
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
    modifier: Modifier = Modifier,
    senderLabel: String? = null,
    timestamp: String? = null,
    collapsedMaxLines: Int = 4,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.card
    val dark = OrbitTheme.isDark
    val linkInk = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50

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
        modifier = modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth()
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowButton)
            .clip(shape)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (dark) 0f else OrbitGlass.SurfaceHighlightLight,
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
                sheen = if (dark) 1f else OrbitGlass.Sheen,
            )
            .padding(spacing.md)
            .semantics { contentDescription = spoken },
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
        horizontalAlignment = Alignment.Start,
    ) {
        if (displaySender != null) {
            Text(
                text = displaySender,
                style = OrbitTheme.extendedTypography.sectionLabel,
                color = content.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = text,
            style = OrbitTheme.extendedTypography.bodyLongForm.copy(fontWeight = FontWeight.Medium),
            color = content.textPrimary,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { layout ->
                if (!expanded) {
                    overflows = layout.hasVisualOverflow
                }
            },
        )

        if (overflows || expanded) {
            Text(
                text = if (expanded) "Show less" else "Show more",
                style = OrbitTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = timestamp,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textTertiary,
                    maxLines = 1,
                )
            }
        }
    }
}

private const val MaxBubbleFillFraction = 0.92f
