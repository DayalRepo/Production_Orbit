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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.markdown.OrbitMarkdown
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProof
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.platform.OrbitBackHandler
import com.orbitai.erp.ui.component.badge.ProjectHealthBadge
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind

/** How many task/issue cards to preview on the unit inside screen before "View all". */
private const val UnitLogPreviewCount = 2

/**
 * Inside screen for a villa or apartment unit.
 *
 * Order: Team → Stageproof → used materials → tasks → issues.
 */
@Composable
fun UnitDetailScreen(
    unit: UnitRecord,
    workItems: List<WorkItemRecord>,
    onBack: () -> Unit,
    onViewWorkItem: (WorkItemRecord) -> Unit,
    onViewAllTasks: () -> Unit,
    onViewAllIssues: () -> Unit,
    modifier: Modifier = Modifier,
    viewerRole: UserRole = UserRole.ProjectManager,
    onWorkItemChange: (WorkItemRecord) -> Unit = {},
) {
    OrbitBackHandler(onBack = onBack)

    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val assignees = rememberAssignedMembers(unit.assigneeIds)
    val tasks = remember(unit, workItems) { workItems.unitTasks(unit) }
    val issues = remember(unit, workItems) { workItems.unitIssues(unit) }
    val usedMaterials = remember(unit, workItems) { workItems.unitUsedMaterials(unit) }
    val materialsMarkdown = remember(usedMaterials) { materialLinesMarkdown(usedMaterials) }
    val teamMarkdown = remember(assignees) { teamMembersMarkdown(assignees) }
    val subtitle = unit.locationSubtitle

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.numberLabel,
                    style = OrbitTheme.typography.titleLarge,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = OrbitTheme.typography.bodyMedium,
                        color = content.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            ProjectHealthBadge(
                health = unit.health,
                size = OrbitBadgeSize.Small,
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
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            SectionHeading("Team")
            if (teamMarkdown.isBlank()) {
                EmptyReviewNote("No team assigned")
            } else {
                OrbitMarkdown(
                    source = teamMarkdown,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SectionRule()
            OrbitStageProof(
                kind = unit.stageProofKind,
                stages = unit.stages,
                completedCount = unit.completedStages,
                modifier = Modifier.fillMaxWidth(),
            )

            SectionRule()
            SectionHeading("Used materials")
            if (materialsMarkdown.isBlank()) {
                EmptyReviewNote("No materials used")
            } else {
                OrbitMarkdown(
                    source = materialsMarkdown,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            SectionRule()
            UnitLogSectionHeader(
                title = "Tasks",
                onViewAll = onViewAllTasks,
                showViewAll = tasks.isNotEmpty(),
            )
            UnitWorkItemLog(
                items = tasks.take(UnitLogPreviewCount),
                emptyNote = "No tasks yet",
                viewerRole = viewerRole,
                onView = onViewWorkItem,
                onWorkItemChange = onWorkItemChange,
            )

            SectionRule()
            UnitLogSectionHeader(
                title = "Issues",
                onViewAll = onViewAllIssues,
                showViewAll = issues.isNotEmpty(),
            )
            UnitWorkItemLog(
                items = issues.take(UnitLogPreviewCount),
                emptyNote = "No issues yet",
                viewerRole = viewerRole,
                onView = onViewWorkItem,
                onWorkItemChange = onWorkItemChange,
            )
        }
    }
}

/**
 * Plain unit issues (or tasks) log — cards only, no outer container.
 * Opened from the unit card Issues metric or from View all.
 */
@Composable
fun UnitWorkLogScreen(
    unit: UnitRecord,
    items: List<WorkItemRecord>,
    title: String,
    onBack: () -> Unit,
    onViewWorkItem: (WorkItemRecord) -> Unit,
    modifier: Modifier = Modifier,
    viewerRole: UserRole = UserRole.ProjectManager,
    onWorkItemChange: (WorkItemRecord) -> Unit = {},
    emptyNote: String = "Nothing here yet",
) {
    OrbitBackHandler(onBack = onBack)
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val safe = WindowInsets.safeDrawing.asPaddingValues()
    val subtitle = unit.locationSubtitle

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = OrbitTheme.typography.titleLarge,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = buildString {
                        append(unit.numberLabel)
                        if (subtitle.isNotBlank()) {
                            append(" · ")
                            append(subtitle)
                        }
                    },
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = spacing.sm,
                    vertical = spacing.screenVertical,
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            UnitWorkItemLog(
                items = items,
                emptyNote = emptyNote,
                viewerRole = viewerRole,
                onView = onViewWorkItem,
                onWorkItemChange = onWorkItemChange,
            )
        }
    }
}

@Composable
private fun UnitLogSectionHeader(
    title: String,
    onViewAll: () -> Unit,
    showViewAll: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        Text(
            text = title.uppercase(),
            style = OrbitTheme.extendedTypography.cardLabel,
            color = OrbitTheme.contentColors.textSecondary,
            modifier = Modifier.weight(1f),
        )
        if (showViewAll) {
            // Same card-corner Primary chip as View / Update — compact, not a pill, no outer glass.
            ActionButton(
                action = ActionKind.View,
                onClick = onViewAll,
                label = "View all",
                size = OrbitButtonSize.Small,
                showIcon = false,
            )
        }
    }
}

@Composable
internal fun UnitWorkItemLog(
    items: List<WorkItemRecord>,
    emptyNote: String,
    viewerRole: UserRole,
    onView: (WorkItemRecord) -> Unit,
    onWorkItemChange: (WorkItemRecord) -> Unit,
) {
    if (items.isEmpty()) {
        EmptyReviewNote(emptyNote)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.md)) {
        items.forEach { record ->
            WorkItemCard(
                record = record,
                viewerRole = viewerRole,
                onUpdate = { onView(record) },
                onEdit = {},
                onDelete = {},
                onView = { onView(record) },
                onStart = {
                    onWorkItemChange(record.withStatus(WorkStatus.InProgress))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
