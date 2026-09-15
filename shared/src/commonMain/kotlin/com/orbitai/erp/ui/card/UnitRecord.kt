package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofKind
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofStep
import com.orbitai.erp.core.designsystem.component.progress.orbitStageProofDefaults
import com.orbitai.erp.core.model.ProjectHealth
import com.orbitai.erp.core.model.ProjectType

/**
 * One villa or apartment unit on a project board — the card twin of [WorkItemRecord].
 *
 * Villas and apartment / building / community units share this model; [projectType] decides the
 * number label wording, stage-proof sequence, and "View villa" vs "View unit" CTA.
 */
data class UnitRecord(
    val id: String,
    /** Villa name or unit number drawn like a task / issue id (`Villa 12`, `A-101`). */
    val numberLabel: String,
    val projectType: ProjectType,
    val health: ProjectHealth,
    /** Planned duration from initiation to target handover, in whole months. */
    val plannedMonths: Int,
    /** Expected delay vs plan; `0` means on track. */
    val delayMonths: Int,
    val issueCount: Int,
    val assigneeIds: Set<String>,
    val stages: List<OrbitStageProofStep>,
    val completedStages: Int,
    val progress: Float,
    val progressDelta: Float? = null,
    val tower: String? = null,
    val floor: String? = null,
) {
    val stageProofKind: OrbitStageProofKind
        get() = when (projectType) {
            ProjectType.Villas -> OrbitStageProofKind.Villa
            ProjectType.ApartmentCommunity -> OrbitStageProofKind.Building
        }

    val plannedDurationLabel: String
        get() = unitMonthsLabel(plannedMonths.coerceAtLeast(0))

    val delayLabel: String
        get() = when {
            delayMonths <= 0 -> "No delay"
            else -> unitMonthsLabel(delayMonths)
        }

    val issuesLabel: String
        get() = when {
            issueCount <= 0 -> "No issues"
            issueCount == 1 -> "1 issue"
            else -> "$issueCount issues"
        }

    val viewActionLabel: String
        get() = when (projectType) {
            ProjectType.Villas -> "View villa"
            ProjectType.ApartmentCommunity -> "View unit"
        }
}

internal fun unitMonthsLabel(months: Int): String = when {
    months <= 0 -> "0 months"
    months == 1 -> "1 month"
    else -> "$months months"
}

fun UnitRecord.withDefaultStages(): UnitRecord {
    if (stages.isNotEmpty()) return this
    return copy(stages = orbitStageProofDefaults(stageProofKind))
}
