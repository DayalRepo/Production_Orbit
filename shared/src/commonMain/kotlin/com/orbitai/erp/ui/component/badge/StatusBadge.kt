package com.orbitai.erp.ui.component.badge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadge
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeEmphasis
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeSize
import com.orbitai.erp.core.model.ApprovalStatus
import com.orbitai.erp.core.model.ProjectHealth
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.StockLevel
import com.orbitai.erp.core.model.WorkStatus

/**
 * App-level badge wrappers.
 *
 * Each one is two lines and that is deliberate — the value is not the code, it is that a screen
 * writes `StatusBadge(task.status)` and cannot get the tone, the glyph or the emphasis wrong. The
 * moment a screen assembles a pill from a tone and an icon itself, two screens start disagreeing
 * about what "Blocked" looks like.
 *
 * @param label overridable for the cases where the record has better wording than the catalogue —
 *   an invoice reading "Awaiting CFO" rather than a generic "Pending".
 */
@Composable
fun StatusBadge(
    kind: BadgeKind,
    modifier: Modifier = Modifier,
    label: String = kind.label,
    size: OrbitBadgeSize = OrbitBadgeSize.Medium,
    emphasis: OrbitBadgeEmphasis = OrbitBadgeEmphasis.Glass,
) {
    OrbitBadge(
        label = label,
        modifier = modifier,
        tone = kind.tone,
        icon = kind.icon,
        size = size,
        emphasis = emphasis,
    )
}

/**
 * A defect severity badge.
 *
 * Critical defaults to solid because it is the one level that must survive being skimmed past on a
 * list of forty inspection results. Everything below it stays on glass, which keeps solid meaning
 * something.
 */
@Composable
fun SeverityBadge(
    severity: Severity,
    modifier: Modifier = Modifier,
    size: OrbitBadgeSize = OrbitBadgeSize.Medium,
    emphasis: OrbitBadgeEmphasis =
        if (severity == Severity.Critical) OrbitBadgeEmphasis.Solid else OrbitBadgeEmphasis.Glass,
) {
    OrbitBadge(
        label = severity.displayName,
        modifier = modifier,
        tone = severity.badgeTone,
        icon = severity.badgeIcon,
        size = size,
        emphasis = emphasis,
    )
}

/** Uses [WorkStatus.displayName] so the badge and the rest of the screen never drift apart. */
@Composable
fun WorkStatusBadge(
    status: WorkStatus,
    modifier: Modifier = Modifier,
    size: OrbitBadgeSize = OrbitBadgeSize.Medium,
) {
    StatusBadge(
        kind = status.badgeKind,
        modifier = modifier,
        label = status.displayName,
        size = size,
        emphasis = if (status == WorkStatus.Blocked) {
            OrbitBadgeEmphasis.Solid
        } else {
            OrbitBadgeEmphasis.Glass
        },
    )
}

@Composable
fun ApprovalStatusBadge(
    status: ApprovalStatus,
    modifier: Modifier = Modifier,
    size: OrbitBadgeSize = OrbitBadgeSize.Medium,
) {
    StatusBadge(
        kind = status.badgeKind,
        modifier = modifier,
        label = status.displayName,
        size = size,
    )
}

/** Out of stock blocks work on site, so it is the one stock level that shouts. */
@Composable
fun StockLevelBadge(
    level: StockLevel,
    modifier: Modifier = Modifier,
    size: OrbitBadgeSize = OrbitBadgeSize.Medium,
) {
    StatusBadge(
        kind = level.badgeKind,
        modifier = modifier,
        label = level.displayName,
        size = size,
        emphasis = if (level == StockLevel.OutOfStock) {
            OrbitBadgeEmphasis.Solid
        } else {
            OrbitBadgeEmphasis.Glass
        },
    )
}

@Composable
fun ProjectHealthBadge(
    health: ProjectHealth,
    modifier: Modifier = Modifier,
    size: OrbitBadgeSize = OrbitBadgeSize.Medium,
) {
    StatusBadge(
        kind = health.badgeKind,
        modifier = modifier,
        label = health.displayName,
        size = size,
    )
}
