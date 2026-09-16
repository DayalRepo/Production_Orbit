package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.card.WorkItemKind
import com.orbitai.erp.ui.card.WorkItemRecord
import com.orbitai.erp.ui.card.sampleIssueApartment
import com.orbitai.erp.ui.card.sampleIssueApartment101
import com.orbitai.erp.ui.card.sampleIssueVilla
import com.orbitai.erp.ui.card.sampleIssueVilla12
import com.orbitai.erp.ui.card.sampleTaskApartment
import com.orbitai.erp.ui.card.sampleTaskVilla
import com.orbitai.erp.ui.form.FormSection

@Composable
fun InvoiceAttachWorkPage(
    projectType: ProjectType,
    attachedTaskIds: Set<String>,
    onTaskToggle: (String) -> Unit,
    attachedIssueIds: Set<String>,
    onIssueToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val catalogue = remember(projectType) { invoiceWorkCatalogue(projectType) }
    val tasks = catalogue.filter { it.kind == WorkItemKind.Task }
    val issues = catalogue.filter { it.kind == WorkItemKind.Issue }
    val selectedIds = catalogue
        .filter { it.id in attachedTaskIds || it.id in attachedIssueIds }
        .map { it.number }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        FormSection(title = "Tasks", showDivider = false) {
            if (tasks.isEmpty()) {
                EmptyAttachNote("No tasks available")
            } else {
                tasks.forEach { item ->
                    AttachWorkIdRow(
                        number = item.number,
                        selected = item.id in attachedTaskIds,
                        kindLabel = "task",
                        onToggle = { onTaskToggle(item.id) },
                    )
                }
            }
        }

        FormSection(title = "Issues") {
            if (issues.isEmpty()) {
                EmptyAttachNote("No issues available")
            } else {
                issues.forEach { item ->
                    AttachWorkIdRow(
                        number = item.number,
                        selected = item.id in attachedIssueIds,
                        kindLabel = "issue",
                        onToggle = { onIssueToggle(item.id) },
                    )
                }
            }
        }

        if (selectedIds.isNotEmpty()) {
            FormSection(title = "Selected") {
                Text(
                    text = selectedIds.joinToString(" · "),
                    style = OrbitTheme.extendedTypography.reference,
                    color = OrbitTheme.contentColors.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun AttachWorkIdRow(
    number: String,
    selected: Boolean,
    kindLabel: String,
    onToggle: () -> Unit,
) {
    val content = OrbitTheme.contentColors
    OrbitCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Select $kindLabel $number"
                role = Role.Checkbox
            },
        onClick = onToggle,
        container = if (selected) {
            OrbitTheme.controlColors.actionContainer.copy(alpha = 0.12f)
        } else {
            OrbitTheme.controlColors.cardContainer
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
        ) {
            OrbitGlyph(
                icon = if (selected) OrbitIcons.Tick else OrbitIcons.Add,
                size = OrbitTheme.sizing.iconMd,
                tint = if (selected) content.iconAccent else content.iconInactive,
                contentDescription = null,
            )
            Text(
                text = number,
                style = OrbitTheme.extendedTypography.reference,
                color = content.textPrimary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EmptyAttachNote(text: String) {
    Text(
        text = text,
        style = OrbitTheme.typography.bodyMedium,
        color = OrbitTheme.contentColors.textTertiary,
        modifier = Modifier.padding(vertical = OrbitTheme.spacing.sm),
    )
}

fun invoiceWorkCatalogue(projectType: ProjectType): List<WorkItemRecord> = when (projectType) {
    ProjectType.Villas -> listOf(sampleTaskVilla(), sampleIssueVilla(), sampleIssueVilla12())
    ProjectType.ApartmentCommunity -> listOf(
        sampleTaskApartment(),
        sampleIssueApartment(),
        sampleIssueApartment101(),
    )
}
