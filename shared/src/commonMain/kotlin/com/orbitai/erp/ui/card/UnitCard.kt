package com.orbitai.erp.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
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
import com.orbitai.erp.ui.component.progress.ProgressCard
import com.orbitai.erp.ui.component.team.TeamAvatarGroup

/**
 * Reusable villa / apartment unit card.
 *
 * Meta order: Issues → Expected delay → Assigned. Issue count and delay figures use theme danger
 * red. Progress sits in its own glass container. Tapping Issues opens the unit issues log.
 */
@Composable
fun UnitCard(
    record: UnitRecord,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
    onIssuesClick: (() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val assignees = rememberAssignedMembers(record.assigneeIds)
    val titleStyle = OrbitTheme.typography.titleLarge
    val alert = unitAlertMetricColor()

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
                    icon = OrbitIcons.BadgeAlert,
                    heading = "Issues",
                    modifier = if (onIssuesClick != null) {
                        Modifier
                            .clickable(
                                role = Role.Button,
                                onClick = onIssuesClick,
                            )
                            .semantics {
                                contentDescription = "Issues, ${record.issuesLabel}. Open issues."
                            }
                    } else {
                        Modifier
                    },
                ) {
                    UnitAlertMetricValue(
                        count = record.issueCount,
                        unitLabel = if (record.issueCount == 1) "issue" else "issues",
                        zeroLabel = "No issues",
                        alertColor = alert,
                    )
                }
                WorkItemMetaRow(
                    icon = OrbitIcons.TimeQuarter,
                    heading = "Expected delay",
                ) {
                    UnitAlertMetricValue(
                        count = record.delayMonths.coerceAtLeast(0),
                        unitLabel = if (record.delayMonths == 1) "month" else "months",
                        zeroLabel = "No delay",
                        alertColor = alert,
                    )
                }
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

            ProgressCard(
                label = "Progress",
                progress = record.progress,
                delta = record.progressDelta,
                comparisonLabel = "vs last week",
                modifier = Modifier.fillMaxWidth(),
            )

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

/** Theme-aware danger red for unit metric figures (light / dark). */
@Composable
internal fun unitAlertMetricColor(): Color =
    OrbitTheme.semanticColors.danger.content

/**
 * Renders a count in alert red and the unit word in primary ink.
 * Zero uses [zeroLabel] in primary (no red).
 */
@Composable
internal fun UnitAlertMetricValue(
    count: Int,
    unitLabel: String,
    zeroLabel: String,
    alertColor: Color,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    val style = OrbitTheme.typography.bodyMedium.copy(
        fontWeight = OrbitTheme.fontWeights.title,
    )
    if (count <= 0) {
        Text(
            text = zeroLabel,
            modifier = modifier,
            style = style,
            color = content.textPrimary,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = count.toString(),
                style = style,
                color = alertColor,
                maxLines = 1,
            )
            Text(
                text = " $unitLabel",
                style = style,
                color = content.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
