package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.dialog.OrbitDialog
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklist
import com.orbitai.erp.core.designsystem.component.markdown.OrbitMarkdown
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.component.attachment.FileAttachmentRow
import com.orbitai.erp.ui.component.button.ActionButtonRow
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.component.input.ManagedDescriptionField

private enum class ReviewConfirm { Approve, Cancel, Rework }

@Composable
internal fun CreatedTaskReviewScreen(
    record: WorkItemRecord,
    viewerRole: UserRole,
    onBack: () -> Unit,
    onApprove: () -> Unit,
    onCancel: () -> Unit,
    onRework: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val scroll = rememberScrollState()
    val noun = if (record.kind == WorkItemKind.Issue) "issue" else "task"
    val review = record.materialReview()
    val givenMarkdown = remember(review.given) { materialLinesMarkdown(review.given) }
    val extraMarkdown = remember(review.extra) { materialLinesMarkdown(review.extra) }
    val started = record.status != WorkStatus.Open && !record.status.isTerminal
    var confirm by remember { mutableStateOf<ReviewConfirm?>(null) }
    var checklistOpen by remember(record.id) { mutableStateOf(false) }
    var photoIndex by remember { mutableStateOf<Int?>(null) }
    var reworkDraft by remember { mutableStateOf("") }

    OrbitBackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe),
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
            if (record.kind == WorkItemKind.Issue) {
                IssueEvidenceSection(record = record)
            } else {
                WorkItemStageTitle(
                    record = record,
                    stageStyle = OrbitTheme.extendedTypography.sectionLabel,
                    titleStyle = OrbitTheme.typography.headlineSmall.copy(
                        fontWeight = OrbitTheme.fontWeights.title,
                    ),
                    maxTitleLines = 4,
                )
            }

            OrderedMaterialsList(
                requests = record.requestedMaterials,
                canReceive = false,
                onReceive = {},
            )

            if (record.reworkNote.isNotBlank()) {
                SectionRule()
                ReworkNoteSection(note = record.reworkNote)
            }

            SectionRule()
            SectionHeading("Checklist")
            OrbitChecklist(
                title = record.checklistTitle,
                items = record.checklistItems,
                onCheckedChange = { _, _ -> },
                expanded = checklistOpen,
                onExpandedChange = { checklistOpen = it },
                modifier = Modifier.fillMaxWidth(),
            )

            SectionRule()
            SectionHeading("Labour used")
            WorkItemMetaRow(
                icon = OrbitIcons.UsersRound,
                heading = "Workers",
                value = if (record.labourUsed <= 0) {
                    "—"
                } else {
                    "${record.labourUsed}"
                },
            )

            SectionRule()
            SectionHeading("Materials given")
            if (givenMarkdown.isBlank()) {
                EmptyReviewNote("No given materials used")
            } else {
                OrbitMarkdown(source = givenMarkdown, modifier = Modifier.fillMaxWidth())
            }

            SectionHeading("Extra materials")
            if (extraMarkdown.isBlank()) {
                EmptyReviewNote("No extra materials")
            } else {
                OrbitMarkdown(source = extraMarkdown, modifier = Modifier.fillMaxWidth())
            }

            if (record.kind != WorkItemKind.Issue) {
                SectionRule()
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
                    EmptyReviewNote("No photos")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
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
                }
            }
        }

        if (started) {
            val footer = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal)
                .padding(vertical = spacing.sm)
            when (viewerRole) {
                UserRole.ProjectManager -> ActionButtonRow(
                    dismiss = ActionKind.Cancel,
                    confirm = ActionKind.Approve,
                    onDismiss = { confirm = ReviewConfirm.Cancel },
                    onConfirm = { confirm = ReviewConfirm.Approve },
                    modifier = footer,
                )
                UserRole.QaQc -> ActionButtonRow(
                    dismiss = ActionKind.Rework,
                    confirm = ActionKind.Approve,
                    onDismiss = {
                        reworkDraft = record.reworkNote
                        confirm = ReviewConfirm.Rework
                    },
                    onConfirm = { confirm = ReviewConfirm.Approve },
                    modifier = footer,
                )
                else -> Unit
            }
        }
    }

    photoIndex?.let { index ->
        WorkItemPhotoViewer(
            photos = record.photos,
            startIndex = index,
            onDismiss = { photoIndex = null },
        )
    }

    when (confirm) {
        ReviewConfirm.Approve -> OrbitConfirmDialog(
            title = "Approve $noun",
            message = "Approve ${record.number} now?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {
                confirm = null
                onApprove()
            },
            onDismiss = { confirm = null },
        )
        ReviewConfirm.Cancel -> OrbitConfirmDialog(
            title = "Cancel $noun",
            message = "Cancel ${record.number}? This cannot be undone.",
            confirmLabel = "Yes",
            dismissLabel = "No",
            destructive = true,
            onConfirm = {
                confirm = null
                onCancel()
            },
            onDismiss = { confirm = null },
        )
        ReviewConfirm.Rework -> {
            val note = reworkDraft.trim()
            OrbitDialog(
                onDismiss = { confirm = null },
                title = "Send $noun back",
                dismissible = false,
                content = {
                    Text(
                        text = "Send ${record.number} back for rework?",
                        style = OrbitTheme.typography.bodyLarge,
                        color = OrbitTheme.contentColors.textPrimary,
                    )
                    ManagedDescriptionField(
                        value = reworkDraft,
                        onValueChange = { reworkDraft = it },
                        label = "Rework note",
                        placeholder = "What they need to fix",
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                actions = {
                    OrbitButton(
                        label = "No",
                        onClick = { confirm = null },
                        variant = OrbitButtonVariant.Secondary,
                        size = OrbitButtonSize.Medium,
                    )
                    OrbitButton(
                        label = "Yes",
                        onClick = {
                            confirm = null
                            onRework(note)
                        },
                        variant = OrbitButtonVariant.Destructive,
                        size = OrbitButtonSize.Medium,
                        state = if (note.isNotEmpty()) {
                            OrbitButtonState.Active
                        } else {
                            OrbitButtonState.Disabled
                        },
                    )
                },
            )
        }
        null -> Unit
    }
}

