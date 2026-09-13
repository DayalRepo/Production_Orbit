package com.orbitai.erp.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklist
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.OrbitQuantityField
import com.orbitai.erp.core.designsystem.component.markdown.OrbitMarkdown
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.component.attachment.ManagedFileUpload
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.component.input.ManagedDescriptionField
import com.orbitai.erp.ui.form.page.MaterialsPage
import kotlin.random.Random

@Composable
internal fun CreatedTaskScreen(
    record: WorkItemRecord,
    viewerRole: UserRole,
    onBack: () -> Unit,
    onRecordChange: (WorkItemRecord) -> Unit,
    onSubmit: () -> Unit,
    onApprove: () -> Unit,
    onCancel: () -> Unit,
    onRework: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (workItemIsReviewer(viewerRole)) {
        CreatedTaskReviewScreen(
            record = record,
            viewerRole = viewerRole,
            onBack = onBack,
            onApprove = onApprove,
            onCancel = onCancel,
            onRework = onRework,
            modifier = modifier,
        )
        return
    }

    val spacing = OrbitTheme.spacing
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    var extraMaterials by remember { mutableStateOf(emptyList<String>()) }
    var extraUnits by remember { mutableStateOf(emptyList<String>()) }
    var confirmSubmit by remember { mutableStateOf(false) }
    var needOpen by remember(record.id) { mutableStateOf(false) }
    var needLines by remember(record.id) {
        mutableStateOf(listOf(OrbitMaterialUsageLine(id = "need-open")))
    }
    val noun = if (record.kind == WorkItemKind.Issue) "issue" else "task"
    val scroll = rememberScrollState()
    val givenMarkdown = remember(record.materials) { materialLinesMarkdown(record.materials) }

    OrbitBackHandler(onBack = onBack)

    LaunchedEffect(record.id) {
        if (record.usedMaterials.isEmpty()) {
            onRecordChange(
                record.withUsedMaterials(listOf(OrbitMaterialUsageLine(id = "u-open"))),
            )
        }
    }

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
                canReceive = true,
                onReceive = { id -> onRecordChange(record.receiveRequestedMaterial(id)) },
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
                onCheckedChange = { id, checked ->
                    onRecordChange(record.withChecklistChecked(id, checked))
                },
                modifier = Modifier.fillMaxWidth(),
            )

            SectionRule()
            MaterialsGivenHeader(
                onNeedClick = if (workItemCanNeed(viewerRole)) {
                    {
                        needLines = listOf(OrbitMaterialUsageLine(id = "need-open"))
                        needOpen = true
                    }
                } else {
                    null
                },
            )
            if (givenMarkdown.isBlank()) {
                EmptyReviewNote("No materials given")
            } else {
                OrbitMarkdown(source = givenMarkdown, modifier = Modifier.fillMaxWidth())
            }

            SectionRule()
            SectionHeading("Update")
            SectionHeading("Labour used")
            OrbitQuantityField(
                value = record.labourUsed,
                onValueChange = { onRecordChange(record.withLabourUsed(it)) },
                label = "Workers",
                range = 0..9_999,
                modifier = Modifier.fillMaxWidth(),
            )

            MaterialsPage(
                label = "Materials log",
                lines = record.usedMaterials,
                extraMaterials = extraMaterials,
                extraUnits = extraUnits,
                onMaterialSelect = { id, material ->
                    onRecordChange(
                        record.withUsedMaterials(
                            upsertUsedLine(record.usedMaterials, id) {
                                it.copy(material = material)
                            },
                        ),
                    )
                },
                onQuantityChange = { id, quantity ->
                    onRecordChange(
                        record.withUsedMaterials(
                            upsertUsedLine(record.usedMaterials, id) {
                                it.copy(quantity = quantity)
                            },
                        ),
                    )
                },
                onUnitSelect = { id, unit ->
                    onRecordChange(
                        record.withUsedMaterials(
                            upsertUsedLine(record.usedMaterials, id) {
                                it.copy(unit = unit)
                            },
                        ),
                    )
                },
                onAddLine = {
                    onRecordChange(
                        record.withUsedMaterials(
                            record.usedMaterials + OrbitMaterialUsageLine(
                                id = "u${Random.nextLong()}",
                            ),
                        ),
                    )
                },
                onRemoveLine = { id ->
                    val next = record.usedMaterials.filterNot { it.id == id }
                    onRecordChange(
                        record.withUsedMaterials(
                            next.ifEmpty { listOf(OrbitMaterialUsageLine(id = "u-open")) },
                        ),
                    )
                },
                onMaterialCreated = { extraMaterials = extraMaterials + it },
                onUnitCreated = { extraUnits = extraUnits + it },
            )

            if (record.kind != WorkItemKind.Issue) {
                SectionRule()
                SectionHeading("Description")
                ManagedDescriptionField(
                    value = record.description,
                    onValueChange = { onRecordChange(record.withDescription(it)) },
                    label = "Description",
                    placeholder = "What was done on site",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SectionRule()
            SectionHeading("Photos")
            ManagedFileUpload(
                modifier = Modifier.fillMaxWidth(),
                browseLabel = "Add photo",
                cameraLabel = "Click photo",
                dropZoneTitle = "Add a photo or take one on site.",
                dropZoneHint = "JPEG or PNG, up to 50 MB.",
                photosOnly = true,
                onCompleted = { id, name, size ->
                    if (record.photos.none { it.id == id }) {
                        onRecordChange(
                            record.withPhotos(
                                record.photos + WorkItemPhoto(id, name, size),
                            ),
                        )
                    }
                },
            )
        }

        ActionButton(
            action = ActionKind.Submit,
            onClick = { confirmSubmit = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.screenHorizontal)
                .padding(vertical = spacing.sm),
        )
    }

    if (needOpen) {
        NeedMaterialDialog(
            lines = needLines,
            extraMaterials = extraMaterials,
            extraUnits = extraUnits,
            onLinesChange = { needLines = it },
            onMaterialCreated = { extraMaterials = extraMaterials + it },
            onUnitCreated = { extraUnits = extraUnits + it },
            onDone = {
                var next = record
                needLines.forEach { next = next.upsertRequestedMaterial(it) }
                onRecordChange(next)
                needOpen = false
            },
            onCancel = { needOpen = false },
        )
    }

    if (confirmSubmit) {
        OrbitConfirmDialog(
            title = "Submit $noun",
            message = "Submit ${record.number} for review?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {
                confirmSubmit = false
                onSubmit()
            },
            onDismiss = { confirmSubmit = false },
        )
    }
}

private fun upsertUsedLine(
    lines: List<OrbitMaterialUsageLine>,
    id: String,
    transform: (OrbitMaterialUsageLine) -> OrbitMaterialUsageLine,
): List<OrbitMaterialUsageLine> {
    val index = lines.indexOfFirst { it.id == id }
    if (index < 0) return lines + transform(OrbitMaterialUsageLine(id = id))
    return lines.mapIndexed { i, line -> if (i == index) transform(line) else line }
}

@Composable
internal fun EmptyReviewNote(text: String) {
    OrbitCard(
        modifier = Modifier.fillMaxWidth(),
        padding = OrbitTheme.spacing.md,
    ) {
        Text(
            text = text,
            style = OrbitTheme.typography.bodyMedium,
            color = OrbitTheme.contentColors.textSecondary,
        )
    }
}

@Composable
internal fun SectionHeading(text: String) {
    Text(
        text = text.uppercase(),
        style = OrbitTheme.extendedTypography.cardLabel,
        color = OrbitTheme.contentColors.textSecondary,
    )
}

@Composable
internal fun ColumnScope.SectionRule() {
    OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
}
