package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.card.InvoiceWorkItemCard
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

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        FormSection(title = "Tasks", showDivider = false) {
            if (tasks.isEmpty()) {
                EmptyAttachNote("No tasks available")
            } else {
                tasks.forEach { item ->
                    InvoiceWorkItemCard(
                        record = item,
                        selected = item.id in attachedTaskIds,
                        onSelect = { onTaskToggle(item.id) },
                    )
                }
            }
        }

        FormSection(title = "Issues") {
            if (issues.isEmpty()) {
                EmptyAttachNote("No issues available")
            } else {
                issues.forEach { item ->
                    InvoiceWorkItemCard(
                        record = item,
                        selected = item.id in attachedIssueIds,
                        onSelect = { onIssueToggle(item.id) },
                    )
                }
            }
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
