package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitHorizontalScrollbar
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentLeading
import com.orbitai.erp.core.designsystem.component.display.OrbitAttachmentRow
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One attachment queued on the composer, identified so remove can target it without relying on
 * filename uniqueness.
 */
@Immutable
data class OrbitComposerAttachment(
    val id: String,
    val fileName: String,
    val fileSize: String,
    val leading: OrbitAttachmentLeading,
)

/**
 * Horizontal file strip drawn above [OrbitMessageField] / [OrbitMessageComposer].
 */
@Composable
fun OrbitComposerAttachmentStrip(
    attachments: List<OrbitComposerAttachment>,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (attachments.isEmpty()) return

    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val scrollState = rememberScrollState()
    val tileWidth = 220.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            attachments.forEach { attachment ->
                OrbitAttachmentRow(
                    fileName = attachment.fileName,
                    fileSize = attachment.fileSize,
                    leading = attachment.leading,
                    expandWidth = false,
                    onRemove = { onRemove(attachment.id) },
                    modifier = Modifier
                        .width(tileWidth)
                        .heightIn(min = sizing.attachmentRowHeight),
                )
            }
        }
        if (scrollState.maxValue > 0) {
            OrbitHorizontalScrollbar(
                scrollState = scrollState,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
