package com.orbitai.erp.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.progress.OrbitStepIndicator
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.core.designsystem.component.input.orbitMaterialUsageLineComplete
import com.orbitai.erp.ui.component.badge.BadgeKind
import com.orbitai.erp.ui.component.badge.PurchaseOrderStatusBadge
import com.orbitai.erp.ui.component.badge.SeverityBadge
import com.orbitai.erp.ui.component.badge.StatusBadge
import com.orbitai.erp.ui.component.badge.WorkStatusBadge
import com.orbitai.erp.ui.component.progress.ProgressSection
import com.orbitai.erp.ui.component.progress.progressAnnouncement
import com.orbitai.erp.ui.component.team.TeamAvatarGroup
import com.orbitai.erp.ui.component.team.TeamMember
import com.orbitai.erp.ui.datetime.orbitRemainingCountdown
import com.orbitai.erp.ui.gallery.rememberGalleryContractors
import com.orbitai.erp.ui.gallery.rememberGalleryProcurementManagers
import com.orbitai.erp.ui.gallery.rememberGallerySiteEngineers
import com.orbitai.erp.ui.gallery.rememberGalleryWarehouseManagers
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val RemainingPlaceholder = "Days · 00d 00h:00m:00s"

@Composable
fun WorkItemCard(
    record: WorkItemRecord,
    viewerRole: UserRole,
    onUpdate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onView: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val assignees = rememberAssignedMembers(record.assigneeIds)
    var remaining by remember(record.dateRange) { mutableStateOf<String?>(null) }
    val percent = (record.progress.coerceIn(0f, 1f) * 100f).roundToInt()
    val showOverflow = viewerRole == UserRole.ProjectManager
    val titleStyle = OrbitTheme.typography.titleLarge
    val isOrder = record.kind == WorkItemKind.PurchaseOrder

    LaunchedEffect(record.dateRange, isOrder) {
        if (isOrder) {
            remaining = null
            return@LaunchedEffect
        }
        val range = record.dateRange
        val end = range?.end
        if (range == null || end == null) {
            remaining = null
            return@LaunchedEffect
        }
        while (true) {
            remaining = orbitRemainingCountdown(allocatedDays = range.days, endDate = end)
            delay(1_000)
        }
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.cardPadding,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = record.numberLabel,
                    modifier = Modifier.weight(1f),
                    style = titleStyle,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (isOrder) {
                    PurchaseOrderStatusBadge(
                        status = record.status,
                        size = OrbitBadgeSize.Small,
                    )
                } else {
                    WorkStatusBadge(
                        status = record.status,
                        size = OrbitBadgeSize.Small,
                    )
                    if (record.requestedMaterials.isNotEmpty()) {
                        StatusBadge(
                            kind = BadgeKind.PurchaseOrder,
                            size = OrbitBadgeSize.Small,
                        )
                    }
                }
                if (showOverflow) {
                    WorkItemOverflowMenu(
                        onEdit = onEdit,
                        onDelete = onDelete,
                        canEdit = record.status != WorkStatus.Completed,
                    )
                }
            }

            CardRule()

            if (!isOrder) {
                if (record.kind == WorkItemKind.Issue) {
                    IssueCardSummary(record = record)
                } else {
                    WorkItemStageTitle(
                        record = record,
                        titleStyle = titleStyle,
                    )
                }
                CardRule()
            }

            Column {
                if (isOrder) {
                    PurchaseOrderMaterialRows(record = record)
                } else {
                    WorkItemMetaRow(
                        icon = OrbitIcons.Clock,
                        heading = "Remaining",
                        value = remaining ?: RemainingPlaceholder,
                    )
                }
                WorkItemMetaRow(
                    icon = OrbitIcons.Location01,
                    heading = if (isOrder) "Project" else "Where",
                    value = record.locationLine,
                )
                if (record.kind == WorkItemKind.Issue) {
                    WorkItemMetaRow(
                        icon = OrbitIcons.BadgeAlert,
                        heading = "Severity",
                    ) {
                        val severity = record.severity
                        if (severity != null) {
                            SeverityBadge(severity = severity)
                        } else {
                            MetaDash()
                        }
                    }
                }
                WorkItemMetaRow(
                    icon = OrbitIcons.UserRound,
                    heading = "Assigned to",
                ) {
                    if (assignees.isEmpty()) {
                        MetaDash()
                    } else {
                        TeamAvatarGroup(
                            members = assignees,
                            background = OrbitTheme.controlColors.cardContainer,
                        )
                    }
                }
            }

            if (!isOrder) {
                CardRule()
                OrbitStepIndicator(
                    steps = record.workflowSteps,
                    currentIndex = record.workflowCurrentIndex,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            CardRule()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = progressAnnouncement(
                            label = "Progress",
                            percent = percent,
                            delta = record.progressDelta,
                            comparisonLabel = "vs last week",
                        )
                    },
            ) {
                ProgressSection(
                    label = "Progress",
                    progress = record.progress,
                    delta = record.progressDelta,
                    comparisonLabel = "vs last week",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            CardRule()

            WorkItemRoleActions(
                record = record,
                viewerRole = viewerRole,
                onStart = onStart,
                onView = onView,
                onUpdate = onUpdate,
            )
        }
    }
}

@Composable
internal fun IssueCardSummary(
    record: WorkItemRecord,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xxs),
    ) {
        Text(
            text = record.issueCardLine,
            style = OrbitTheme.typography.bodyLarge.copy(
                fontWeight = OrbitTheme.fontWeights.title,
            ),
            color = content.textPrimary,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
        )
        Text(
            text = workItemPhotoCountLabel(record.photos.size),
            style = OrbitTheme.extendedTypography.cardLabel,
            color = content.textSecondary,
        )
    }
}

@Composable
internal fun WorkItemStageTitle(
    record: WorkItemRecord,
    modifier: Modifier = Modifier,
    stageStyle: TextStyle = OrbitTheme.extendedTypography.cardLabel,
    titleStyle: TextStyle = OrbitTheme.typography.titleLarge,
    maxTitleLines: Int = 2,
) {
    val content = OrbitTheme.contentColors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.xxs),
    ) {
        Text(
            text = record.stageHeading.uppercase(),
            style = stageStyle,
            color = content.textSecondary,
        )
        Text(
            text = record.taskTitle,
            style = titleStyle,
            color = content.textPrimary,
            maxLines = maxTitleLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CardRule() {
    OrbitDivider(
        modifier = Modifier.padding(vertical = OrbitTheme.spacing.xxs),
        color = OrbitTheme.controlColors.dividerElevated,
    )
}

@Composable
private fun MetaDash() {
    Text(
        text = "—",
        style = OrbitTheme.typography.bodyMedium.copy(
            fontWeight = OrbitTheme.fontWeights.title,
        ),
        color = OrbitTheme.contentColors.textTertiary,
    )
}

@Composable
internal fun PurchaseOrderMaterialRows(
    record: WorkItemRecord,
    modifier: Modifier = Modifier,
) {
    val lines = record.materials.filter(::orbitMaterialUsageLineComplete).take(1)
    Column(modifier = modifier) {
        if (lines.isEmpty()) {
            WorkItemMetaRow(
                icon = OrbitIcons.SquareDashed,
                heading = "Materials",
                value = "—",
            )
            WorkItemMetaRow(
                icon = OrbitIcons.PlusMinus,
                heading = "Quantity",
                value = "—",
            )
            WorkItemMetaRow(
                icon = OrbitIcons.Weight,
                heading = "Units",
                value = "—",
            )
        } else {
            lines.forEach { line ->
                WorkItemMetaRow(
                    icon = OrbitIcons.SquareDashed,
                    heading = "Materials",
                    value = line.material.orEmpty().ifBlank { "—" },
                )
                WorkItemMetaRow(
                    icon = OrbitIcons.PlusMinus,
                    heading = "Quantity",
                    value = line.quantity.toString(),
                )
                WorkItemMetaRow(
                    icon = OrbitIcons.Weight,
                    heading = "Units",
                    value = line.unit.orEmpty().ifBlank { "—" },
                )
            }
        }
    }
}

@Composable
internal fun rememberAssignedMembers(ids: Set<String>): List<TeamMember> {
    val siteEngineers = rememberGallerySiteEngineers()
    val contractors = rememberGalleryContractors()
    val procurement = rememberGalleryProcurementManagers()
    val warehouse = rememberGalleryWarehouseManagers()
    return remember(ids, siteEngineers, contractors, procurement, warehouse) {
        (siteEngineers + contractors + procurement + warehouse)
            .filter { it.id in ids }
            .map { member ->
                TeamMember(
                    id = member.id,
                    name = member.name,
                    phone = member.mobile,
                    role = member.role,
                    avatar = member.avatar,
                )
            }
    }
}
