package com.orbitai.erp.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow

/** Raised-issue description and photos. Hidden on tasks. */
@Composable
internal fun IssueEvidenceSection(
    record: WorkItemRecord,
    modifier: Modifier = Modifier,
) {
    if (record.kind != WorkItemKind.Issue) return
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        SectionHeading("Description")
        Text(
            text = record.description.trim().ifBlank { "No description" },
            style = OrbitTheme.typography.bodyLarge.copy(
                fontWeight = OrbitTheme.fontWeights.body,
            ),
            color = if (record.description.isBlank()) {
                content.textSecondary
            } else {
                content.textPrimary
            },
        )

        SectionHeading("Photos")
        if (record.photos.isEmpty()) {
            Text(
                text = workItemPhotoCountLabel(0),
                style = OrbitTheme.typography.bodyMedium.copy(
                    fontWeight = OrbitTheme.fontWeights.body,
                ),
                color = content.textSecondary,
            )
        } else {
            var photoIndex by remember { mutableStateOf<Int?>(null) }
            Column(verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm)) {
                record.photos.forEachIndexed { index, photo ->
                    FileAttachmentRow(
                        fileName = photo.fileName,
                        fileSize = photo.fileSize,
                        preview = workItemPhotoPainter(photo),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { photoIndex = index },
                    )
                }
            }
            photoIndex?.let { index ->
                WorkItemPhotoViewer(
                    photos = record.photos,
                    startIndex = index,
                    onDismiss = { photoIndex = null },
                )
            }
        }
    }
}

@Composable
internal fun ReworkNoteSection(note: String, modifier: Modifier = Modifier) {
    if (note.isBlank()) return
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        SectionHeading("Rework")
        Text(
            text = note,
            style = OrbitTheme.typography.bodyLarge.copy(
                fontWeight = OrbitTheme.fontWeights.body,
            ),
            color = OrbitTheme.contentColors.textPrimary,
        )
    }
}
