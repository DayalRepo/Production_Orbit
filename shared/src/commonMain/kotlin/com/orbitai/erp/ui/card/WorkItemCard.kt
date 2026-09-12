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
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeEmphasis
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProof
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.ui.component.badge.SeverityBadge
import com.orbitai.erp.ui.component.badge.WorkStatusBadge
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.component.progress.ProgressSection
import com.orbitai.erp.ui.component.progress.progressAnnouncement
import com.orbitai.erp.ui.component.team.TeamAvatarGroup
import com.orbitai.erp.ui.component.team.TeamMember
import com.orbitai.erp.ui.datetime.orbitRemainingCountdown
import com.orbitai.erp.ui.gallery.rememberGalleryContractors
import com.orbitai.erp.ui.gallery.rememberGallerySiteEngineers
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val RemainingPlaceholder = "Days · 00d 00h:00m:00s"

@Composable
fun WorkItemCard(
    record: WorkItemRecord,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val assignees = rememberAssignedMembers(record.assigneeIds)
    var remaining by remember(record.dateRange) { mutableStateOf<String?>(null) }
    val percent = (record.progress.coerceIn(0f, 1f) * 100f).roundToInt()
    val numberLabel = if (record.kind == WorkItemKind.Issue) {
        "Issue ${record.number}"
    } else {
        "Task ${record.number}"
    }

    LaunchedEffect(record.dateRange) {
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
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                ) {
                    Text(
                        text = record.stageHeading.uppercase(),
                        style = OrbitTheme.extendedTypography.cardLabel,
                        color = content.textSecondary,
                    )
                    Text(
                        text = record.taskTitle,
                        style = OrbitTheme.typography.titleLarge,
                        color = content.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = numberLabel.uppercase(),
                        style = OrbitTheme.extendedTypography.cardLabel,
                        color = content.textTertiary,
                    )
                }
                WorkStatusBadge(
                    status = record.status,
                    size = OrbitBadgeSize.Medium,
                )
            }

            CardRule()

            WorkItemMetaRow(
                icon = OrbitIcons.Clock,
                heading = "Remaining",
                value = remaining ?: RemainingPlaceholder,
            )
            WorkItemMetaRow(
                icon = OrbitIcons.Location01,
                heading = "Where",
                value = record.locationLine,
            )
            if (record.kind == WorkItemKind.Issue) {
                WorkItemMetaRow(
                    icon = OrbitIcons.BadgeAlert,
                    heading = "Severity",
                ) {
                    val severity = record.severity
                    if (severity != null) {
                        SeverityBadge(
                            severity = severity,
                            size = OrbitBadgeSize.Small,
                            emphasis = OrbitBadgeEmphasis.Solid,
                        )
                    } else {
                        MetaDash()
                    }
                }
            }
            WorkItemMetaRow(
                icon = OrbitIcons.UserRound,
                heading = "Assigned",
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

            CardRule()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                OrbitStageProof(
                    kind = record.stageProofKind,
                    completedCount = record.stageProofCompletedCount,
                    modifier = Modifier.fillMaxWidth(),
                )
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
            }

            CardRule()

            ActionButton(
                action = ActionKind.Update,
                onClick = onUpdate,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
private fun rememberAssignedMembers(ids: Set<String>): List<TeamMember> {
    val siteEngineers = rememberGallerySiteEngineers()
    val contractors = rememberGalleryContractors()
    return remember(ids, siteEngineers, contractors) {
        (siteEngineers + contractors)
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
