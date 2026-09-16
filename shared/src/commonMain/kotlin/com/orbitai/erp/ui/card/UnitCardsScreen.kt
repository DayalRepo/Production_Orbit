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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.platform.OrbitBackHandler

private enum class UnitLogKind { Tasks, Issues }

/**
 * Unit list → unit detail / issues log / tasks log → nested task/issue detail.
 */
@Composable
fun UnitCardsScreen(
    units: List<UnitRecord>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Units",
    workItems: List<WorkItemRecord> = unitWorkLogSamples(),
) {
    val liveItems = remember(workItems) {
        mutableStateListOf<WorkItemRecord>().also { it.addAll(workItems) }
    }
    var openUnitId by remember { mutableStateOf<String?>(null) }
    var openLog by remember { mutableStateOf<UnitLogKind?>(null) }
    var openWorkItemId by remember { mutableStateOf<String?>(null) }

    fun replaceWorkItem(next: WorkItemRecord) {
        val index = liveItems.indexOfFirst { it.id == next.id }
        if (index >= 0) liveItems[index] = next
    }

    val openUnit = openUnitId?.let { id -> units.firstOrNull { it.id == id } }
    val openWorkItem = openWorkItemId?.let { id -> liveItems.firstOrNull { it.id == id } }

    if (openWorkItem != null) {
        CreatedTaskScreen(
            record = openWorkItem,
            viewerRole = UserRole.ProjectManager,
            onBack = { openWorkItemId = null },
            onRecordChange = { next -> replaceWorkItem(next) },
            onSubmit = {
                replaceWorkItem(openWorkItem.withStatus(WorkStatus.InReview))
                openWorkItemId = null
            },
            onApprove = {
                replaceWorkItem(openWorkItem.withStatus(WorkStatus.Completed))
                openWorkItemId = null
            },
            onCancel = {
                replaceWorkItem(openWorkItem.withStatus(WorkStatus.Cancelled))
                openWorkItemId = null
            },
            onRework = { note ->
                replaceWorkItem(openWorkItem.withRework(note))
                openWorkItemId = null
            },
            modifier = modifier,
        )
        return
    }

    if (openUnit != null && openLog != null) {
        val items = when (openLog) {
            UnitLogKind.Tasks -> liveItems.unitTasks(openUnit)
            UnitLogKind.Issues -> liveItems.unitIssues(openUnit)
            null -> emptyList()
        }
        UnitWorkLogScreen(
            unit = openUnit,
            items = items,
            title = when (openLog) {
                UnitLogKind.Tasks -> "Tasks"
                UnitLogKind.Issues -> "Issues"
                null -> ""
            },
            onBack = { openLog = null },
            onViewWorkItem = { item -> openWorkItemId = item.id },
            onWorkItemChange = { next -> replaceWorkItem(next) },
            emptyNote = when (openLog) {
                UnitLogKind.Tasks -> "No tasks yet"
                UnitLogKind.Issues -> "No issues yet"
                null -> "Nothing here yet"
            },
            modifier = modifier,
        )
        return
    }

    if (openUnit != null) {
        UnitDetailScreen(
            unit = openUnit,
            workItems = liveItems.toList(),
            onBack = {
                openLog = null
                openUnitId = null
            },
            onViewWorkItem = { item -> openWorkItemId = item.id },
            onViewAllTasks = { openLog = UnitLogKind.Tasks },
            onViewAllIssues = { openLog = UnitLogKind.Issues },
            onWorkItemChange = { next -> replaceWorkItem(next) },
            modifier = modifier,
        )
        return
    }

    OrbitBackHandler(onBack = onBack)
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OrbitTheme.colorScheme.background)
            .padding(safe),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = spacing.screenHorizontal,
                    vertical = spacing.sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            OrbitIconButton(
                contentDescription = "Back",
                onClick = onBack,
                icon = OrbitIcons.ArrowLeft,
                style = OrbitIconButtonStyle.Neutral,
            )
            Text(
                text = title,
                style = OrbitTheme.typography.titleLarge,
                color = content.textPrimary,
                modifier = Modifier.weight(1f),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = spacing.sm,
                    vertical = spacing.screenVertical,
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            units.forEach { unit ->
                UnitCard(
                    record = unit,
                    onView = {
                        openLog = null
                        openUnitId = unit.id
                    },
                    onIssuesClick = {
                        openUnitId = unit.id
                        openLog = UnitLogKind.Issues
                    },
                )
            }
        }
    }
}
