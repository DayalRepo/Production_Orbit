package com.orbitai.erp.ui.status

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.orbitai.erp.core.designsystem.component.badge.OrbitBadgeEmphasis
import com.orbitai.erp.core.designsystem.theme.ColorPair
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.model.ApprovalStatus
import com.orbitai.erp.core.model.Priority
import com.orbitai.erp.core.model.ProjectHealth
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.StockLevel
import com.orbitai.erp.core.model.WorkStatus

/**
 * Maps ERP enums onto design tokens.
 *
 * This lives in `:shared` rather than `:core:designsystem` on purpose. The design system takes
 * [ColorPair]s and knows nothing about tasks, defects or stock, which is what lets it be reused
 * and what stops a component's signature from growing a new parameter every time the domain does.
 * All of the enum-to-token knowledge is in this one file.
 */

val WorkStatus.colors: ColorPair
    @Composable @ReadOnlyComposable get() = with(OrbitTheme.semanticColors) {
        when (this@colors) {
            WorkStatus.Open -> statusOpen
            WorkStatus.InProgress -> statusInProgress
            WorkStatus.Blocked -> statusBlocked
            WorkStatus.InReview -> statusInReview
            WorkStatus.Completed -> statusCompleted
            WorkStatus.Cancelled -> statusCancelled
        }
    }

/** Blocked work is the only status that should shout from a list. */
val WorkStatus.emphasis: OrbitBadgeEmphasis
    get() = if (this == WorkStatus.Blocked) OrbitBadgeEmphasis.Solid else OrbitBadgeEmphasis.Glass

/**
 * Overdue is a derived state rather than a [WorkStatus] value: a task can be Open *and* overdue.
 * Screens that compute it read this token directly.
 */
val overdueColors: ColorPair
    @Composable @ReadOnlyComposable get() = OrbitTheme.semanticColors.statusOverdue

val Severity.colors: ColorPair
    @Composable @ReadOnlyComposable get() = with(OrbitTheme.semanticColors) {
        when (this@colors) {
            Severity.Low -> severityLow
            Severity.Medium -> severityMedium
            Severity.High -> severityHigh
            Severity.Critical -> severityCritical
        }
    }


/** Priority reuses the severity ramp — four ascending steps of the same visual language. */
val Priority.colors: ColorPair
    @Composable @ReadOnlyComposable get() = with(OrbitTheme.semanticColors) {
        when (this@colors) {
            Priority.Low -> severityLow
            Priority.Medium -> severityMedium
            Priority.High -> severityHigh
            Priority.Urgent -> severityCritical
        }
    }


val ProjectHealth.colors: ColorPair
    @Composable @ReadOnlyComposable get() = with(OrbitTheme.semanticColors) {
        when (this@colors) {
            ProjectHealth.OnTrack -> healthOnTrack
            ProjectHealth.AtRisk -> healthAtRisk
            ProjectHealth.Delayed -> healthDelayed
        }
    }

val StockLevel.colors: ColorPair
    @Composable @ReadOnlyComposable get() = with(OrbitTheme.semanticColors) {
        when (this@colors) {
            StockLevel.Healthy -> stockHealthy
            StockLevel.Low -> stockLow
            StockLevel.OutOfStock -> stockOut
        }
    }

/** Out of stock blocks work on site, so it gets solid emphasis. */
val StockLevel.emphasis: OrbitBadgeEmphasis
    get() = if (this == StockLevel.OutOfStock) {
        OrbitBadgeEmphasis.Solid
    } else {
        OrbitBadgeEmphasis.Glass
    }

val ApprovalStatus.colors: ColorPair
    @Composable @ReadOnlyComposable get() = with(OrbitTheme.semanticColors) {
        when (this@colors) {
            ApprovalStatus.Draft -> neutral
            ApprovalStatus.Pending -> warning
            ApprovalStatus.Approved -> success
            ApprovalStatus.Rejected -> danger
        }
    }
