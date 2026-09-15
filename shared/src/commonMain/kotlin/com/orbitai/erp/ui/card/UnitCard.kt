package com.orbitai.erp.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProof
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import com.orbitai.erp.ui.component.badge.ProjectHealthBadge
import com.orbitai.erp.ui.component.button.ActionButton
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.component.progress.ProgressSection
import com.orbitai.erp.ui.component.progress.progressAnnouncement
import com.orbitai.erp.ui.component.team.TeamAvatarGroup
import kotlin.math.roundToInt

/**
 * Reusable villa / apartment unit card.
 *
 * Mirrors [WorkItemCard] rhythm: id + badge, meta rows, stage proof, progress with delta,
 * then a single view CTA. One component covers both [com.orbitai.erp.core.model.ProjectType]s.
 */
@Composable
fun UnitCard(
    record: UnitRecord,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val assignees = rememberAssignedMembers(record.assigneeIds)
    val percent = (record.progress.coerceIn(0f, 1f) * 100f).roundToInt()
    val titleStyle = OrbitTheme.typography.titleLarge

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
                ProjectHealthBadge(
                    health = record.health,
                    size = OrbitBadgeSize.Small,
                )
            }

            OrbitCardRule()

            Column {
                WorkItemMetaRow(
                    icon = OrbitIcons.CalendarSchedule,
                    heading = "Initiated & Target",
                    value = record.plannedDurationLabel,
                )
                WorkItemMetaRow(
                    icon = OrbitIcons.TimeQuarter,
                    heading = "Expected delay",
                    value = record.delayLabel,
                )
                WorkItemMetaRow(
                    icon = OrbitIcons.BadgeAlert,
                    heading = "Issues",
                    value = record.issuesLabel,
                )
                WorkItemMetaRow(
                    icon = OrbitIcons.UserRound,
                    heading = "Assigned to",
                ) {
                    if (assignees.isEmpty()) {
                        OrbitCardMetaDash()
                    } else {
                        TeamAvatarGroup(
                            members = assignees,
                            background = OrbitTheme.controlColors.cardContainer,
                        )
                    }
                }
            }

            OrbitCardRule()

            OrbitStageProof(
                kind = record.stageProofKind,
                stages = record.stages,
                completedCount = record.completedStages,
                modifier = Modifier.fillMaxWidth(),
            )

            OrbitCardRule()

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

            OrbitCardRule()

            ActionButton(
                action = ActionKind.View,
                onClick = onView,
                modifier = Modifier.fillMaxWidth(),
                label = record.viewActionLabel,
                showIcon = false,
            )
        }
    }
}
