package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Message field with a horizontal attachment strip stacked above it.
 *
 * Attachments sit outside the composer glass so the draft pill stays clean while files scroll
 * sideways above it — the layout used by chat and AI prompt composers.
 */
@Composable
fun OrbitMessageComposer(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onAttachClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    mode: OrbitComposerMode = OrbitComposerMode.Text,
    attachExpanded: Boolean = false,
    enabled: Boolean = true,
    maxLines: Int = 5,
    onCancelRecording: () -> Unit = {},
    onPauseRecording: () -> Unit = {},
    attachMenu: (@Composable () -> Unit)? = null,
    attachments: List<OrbitComposerAttachment> = emptyList(),
    onRemoveAttachment: (String) -> Unit = {},
    hasQueuedContent: Boolean = false,
) {
    val spacing = OrbitTheme.spacing
    val hasAttachments = attachments.isNotEmpty()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (hasAttachments) spacing.sm else spacing.xs),
    ) {
        if (hasAttachments) {
            OrbitComposerAttachmentStrip(
                attachments = attachments,
                onRemove = onRemoveAttachment,
            )
        }

        OrbitMessageField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            onSend = onSend,
            onMicClick = onMicClick,
            onAttachClick = onAttachClick,
            placeholder = placeholder,
            mode = mode,
            attachExpanded = attachExpanded,
            enabled = enabled,
            maxLines = maxLines,
            onCancelRecording = onCancelRecording,
            onPauseRecording = onPauseRecording,
            attachMenu = attachMenu,
            hasQueuedContent = hasQueuedContent || hasAttachments,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
