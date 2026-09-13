package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.attachment.ManagedFileUpload
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.component.input.ManagedDescriptionField

@Composable
internal fun CreatedOrderScreen(
    record: WorkItemRecord,
    viewerRole: UserRole,
    onBack: () -> Unit,
    onRecordChange: (WorkItemRecord) -> Unit,
    onOrderDone: () -> Unit,
    onReceived: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val scroll = rememberScrollState()
    val canEdit = (
        viewerRole == UserRole.ProcurementManager ||
            viewerRole == UserRole.WarehouseManager
        ) && !record.status.isTerminal
    val canOrderDone = viewerRole == UserRole.ProcurementManager &&
        record.status != WorkStatus.InReview &&
        !record.status.isTerminal
    val canReceive = viewerRole == UserRole.WarehouseManager && !record.status.isTerminal
    var confirmDone by remember { mutableStateOf(false) }
    var confirmReceived by remember { mutableStateOf(false) }
    var photoIndex by remember { mutableStateOf<Int?>(null) }

    OrbitBackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe)
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrbitIconButton(
                contentDescription = "Back",
                onClick = onBack,
                icon = OrbitIcons.ArrowLeft,
                style = OrbitIconButtonStyle.Neutral,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal)
                .verticalScroll(scroll)
                .padding(bottom = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            SectionHeading("Description")
            if (canEdit) {
                ManagedDescriptionField(
                    value = record.description,
                    onValueChange = { onRecordChange(record.withDescription(it)) },
                    label = "Description",
                    placeholder = "What was ordered and what is on the way",
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
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
            }

            SectionRule()
            SectionHeading("Photos")
            if (canEdit) {
                ManagedFileUpload(
                    modifier = Modifier.fillMaxWidth(),
                    browseLabel = "Upload photo",
                    cameraLabel = "Click photo",
                    dropZoneTitle = "Add a photo or take one of the order.",
                    dropZoneHint = "JPEG or PNG, up to 50 MB.",
                    photosOnly = true,
                    onCompleted = { id, name, size ->
                        if (record.photos.none { it.id == id }) {
                            onRecordChange(record.withOrderPhoto(WorkItemPhoto(id, name, size)))
                        }
                    },
                )
            }
            if (record.photos.isEmpty()) {
                Text(
                    text = workItemPhotoCountLabel(0),
                    style = OrbitTheme.typography.bodyMedium.copy(
                        fontWeight = OrbitTheme.fontWeights.body,
                    ),
                    color = content.textSecondary,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    record.photos.forEachIndexed { index, photo ->
                        FileAttachmentRow(
                            fileName = photo.fileName,
                            fileSize = photo.fileSize,
                            preview = workItemPhotoPainter(photo),
                            modifier = Modifier.fillMaxWidth(),
                            onRemove = if (canEdit) {
                                { onRecordChange(record.withPhotos(record.photos.filterNot { it.id == photo.id })) }
                            } else {
                                null
                            },
                            onClick = { photoIndex = index },
                        )
                    }
                }
            }
        }

        if (canOrderDone) {
            ActionButton(
                action = ActionKind.Done,
                label = "Order done",
                onClick = { confirmDone = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenHorizontal)
                    .padding(vertical = spacing.sm),
            )
        } else if (canReceive) {
            ActionButton(
                action = ActionKind.Receive,
                onClick = { confirmReceived = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenHorizontal)
                    .padding(vertical = spacing.sm),
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

    if (confirmDone) {
        OrbitConfirmDialog(
            title = "Order done",
            message = "Mark ${record.number} done?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {
                confirmDone = false
                onOrderDone()
            },
            onDismiss = { confirmDone = false },
        )
    }

    if (confirmReceived) {
        OrbitConfirmDialog(
            title = "Received",
            message = "Mark ${record.number} received?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {
                confirmReceived = false
                onReceived()
            },
            onDismiss = { confirmReceived = false },
        )
    }
}
