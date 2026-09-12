package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.progress.OrbitStageProofKind
import com.orbitai.erp.core.designsystem.component.progress.orbitStageProofDefaults
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.ui.component.dropdown.WorkStage
import com.orbitai.erp.ui.form.RaiseIssueDraft
import com.orbitai.erp.ui.form.WorkItemDraft
import kotlin.random.Random

enum class WorkItemKind { Task, Issue }

data class WorkItemRecord(
    val id: String,
    val number: String,
    val kind: WorkItemKind,
    val projectType: ProjectType,
    val stage: WorkStage?,
    val customStage: String?,
    val task: String?,
    val villa: String?,
    val floor: String?,
    val tower: String?,
    val apartmentUnit: String?,
    val dateRange: OrbitDateRange?,
    val assigneeIds: Set<String>,
    val progress: Float,
    val progressDelta: Float?,
    val severity: Severity?,
    val status: WorkStatus,
) {
    /** High-level stage name drawn in caps above the task title. */
    val stageHeading: String
        get() = customStage?.takeIf { it.isNotBlank() }
            ?: stage?.label?.takeIf { it.isNotBlank() }
            ?: "Stage"

    /** Task name under the stage heading. */
    val taskTitle: String
        get() = task?.takeIf { it.isNotBlank() } ?: "Task"

    val title: String
        get() = taskTitle

    val locationLine: String
        get() {
            val parts = when (projectType) {
                ProjectType.Villas -> listOfNotNull(
                    villa?.takeIf { it.isNotBlank() },
                    floor?.takeIf { it.isNotBlank() },
                )
                ProjectType.ApartmentCommunity -> listOfNotNull(
                    tower?.takeIf { it.isNotBlank() },
                    floor?.takeIf { it.isNotBlank() },
                    apartmentUnit?.takeIf { it.isNotBlank() },
                )
            }
            return parts.joinToString(" · ").ifBlank { "—" }
        }

    val stageProofKind: OrbitStageProofKind
        get() = when (projectType) {
            ProjectType.Villas -> OrbitStageProofKind.Villa
            ProjectType.ApartmentCommunity -> OrbitStageProofKind.Building
        }

    /**
     * How many high-level stages are already behind the current one, so [OrbitStageProof]
     * highlights the right mark. A completed item counts every stage as done.
     */
    val stageProofCompletedCount: Int
        get() {
            val steps = orbitStageProofDefaults(stageProofKind)
            if (status == WorkStatus.Completed) return steps.size
            val index = steps.indexOfFirst { it.code == stage?.code }
            return if (index < 0) 0 else index
        }
}

fun WorkItemDraft.toRecord(
    kind: WorkItemKind,
    projectType: ProjectType,
    severity: Severity? = null,
    status: WorkStatus = WorkStatus.Open,
): WorkItemRecord {
    val total = checklistItems.size
    val checked = checklistItems.count { it.checked }
    val progress = if (total == 0) 0f else checked / total.toFloat()
    return WorkItemRecord(
        id = "w${Random.nextLong()}",
        number = nextWorkItemNumber(kind),
        kind = kind,
        projectType = projectType,
        stage = stage,
        customStage = customStage,
        task = task,
        villa = villa,
        floor = floor,
        tower = tower,
        apartmentUnit = apartmentUnit,
        dateRange = dateRange,
        assigneeIds = siteEngineerIds + contractorIds,
        progress = progress,
        progressDelta = null,
        severity = severity,
        status = status,
    )
}

fun RaiseIssueDraft.toRecord(projectType: ProjectType): WorkItemRecord =
    work.toRecord(
        kind = WorkItemKind.Issue,
        projectType = projectType,
        severity = severity,
    )

internal fun nextWorkItemNumber(kind: WorkItemKind): String {
    val prefix = if (kind == WorkItemKind.Issue) "I" else "T"
    return "$prefix-${Random.nextInt(1000, 10000)}"
}
