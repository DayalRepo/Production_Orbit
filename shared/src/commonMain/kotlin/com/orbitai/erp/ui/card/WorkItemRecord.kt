package com.orbitai.erp.ui.card

import com.orbitai.erp.core.designsystem.component.datetime.OrbitDateRange
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.orbitMaterialUsageLineComplete
import com.orbitai.erp.core.designsystem.component.progress.OrbitStep
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.core.model.WorkStatus
import com.orbitai.erp.ui.component.dropdown.WorkStage
import com.orbitai.erp.ui.datetime.toLocalDate
import com.orbitai.erp.ui.datetime.toOrbitCalendarDate
import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.model.UserRole
import com.orbitai.erp.ui.form.DraftPhoto
import com.orbitai.erp.ui.form.MaterialsOrderDraft
import com.orbitai.erp.ui.form.RaiseIssueDraft
import com.orbitai.erp.ui.form.WorkItemDraft
import com.orbitai.erp.ui.form.completeMaterialLines
import kotlinx.datetime.LocalDate
import kotlin.random.Random

data class WorkItemPhoto(
    val id: String,
    val fileName: String,
    val fileSize: String,
)

data class WorkItemComment(
    val id: String,
    val author: String,
    val body: String,
    val time: String,
    val mine: Boolean = false,
    val fromAi: Boolean = false,
)

enum class WorkItemKind { Task, Issue, PurchaseOrder }

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
    val projectName: String? = null,
    val dateRange: OrbitDateRange?,
    val assigneeIds: Set<String>,
    val progress: Float,
    val progressDelta: Float?,
    val severity: Severity?,
    val status: WorkStatus,
    val materials: List<OrbitMaterialUsageLine> = emptyList(),
    val usedMaterials: List<OrbitMaterialUsageLine> = emptyList(),
    val labourUsed: Int = 0,
    val description: String = "",
    val raisedIssues: List<String> = emptyList(),
    val checklistTitle: String = "Checklist",
    val checklistItems: List<OrbitChecklistItem> = emptyList(),
    val photos: List<WorkItemPhoto> = emptyList(),
    val comments: List<WorkItemComment> = emptyList(),
    val reworkNote: String = "",
    val requestedMaterials: List<WorkItemMaterialRequest> = emptyList(),
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

    val numberLabel: String
        get() = number

    val locationLine: String
        get() {
            if (kind == WorkItemKind.PurchaseOrder) {
                return projectName?.takeIf { it.isNotBlank() } ?: "—"
            }
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

    /**
     * Gallery workflow track for [OrbitStepIndicator]:
     * Scheduled → In progress → Inspection → Approval → Completed.
     */
    val workflowSteps: List<OrbitStep>
        get() = workItemWorkflowSteps(status, dateRange)

    val workflowCurrentIndex: Int
        get() = workItemWorkflowIndex(status)

    val inboxUnreadCount: Int
        get() = 0

    /** One-line card copy: an AI rewrite of the raised description, sized to fit the card. */
    val issueCardLine: String
        get() = rewriteIssueCardLine(description)
}

/** Characters that fit one un-truncated line on the created issue card. */
internal const val IssueCardLineMaxChars = 42

private val IssueCardFillers = setOf(
    "the", "a", "an", "and", "of", "at", "to", "for", "on", "in", "with",
    "that", "this", "is", "was", "were", "been", "being", "from",
)

internal fun rewriteIssueCardLine(
    description: String,
    maxChars: Int = IssueCardLineMaxChars,
): String {
    val clean = description.trim().replace(Regex("\\s+"), " ")
    if (clean.isEmpty()) return "—"
    val first = clean.split(Regex("[.!?\n]")).firstOrNull { it.isNotBlank() }?.trim() ?: clean
    val titled = first.replaceFirstChar { it.uppercase() }
    if (titled.length <= maxChars) return titled
    val compact = first.split(' ')
        .filterIndexed { index, word -> index == 0 || word.lowercase() !in IssueCardFillers }
        .joinToString(" ")
        .replaceFirstChar { it.uppercase() }
    return fitIssueCardLine(if (compact.length <= maxChars) compact else titled, maxChars)
}

internal fun fitIssueCardLine(text: String, maxChars: Int = IssueCardLineMaxChars): String {
    if (text.length <= maxChars) return text
    val cut = text.take(maxChars).trimEnd()
    val space = cut.lastIndexOf(' ')
    return if (space >= 8) cut.take(space) else cut
}

internal fun workItemPhotoCountLabel(count: Int): String = "$count PHOTOS"

internal fun splitAssigneeIds(ids: Set<String>): Pair<Set<String>, Set<String>> {
    val site = ids.filter { id ->
        MockDirectory.Users.firstOrNull { it.id == id }?.role == UserRole.SiteEngineer
    }.toSet()
    return site to (ids - site)
}

internal fun splitSupplyAssigneeIds(ids: Set<String>): Pair<Set<String>, Set<String>> {
    val procurement = ids.filter { id ->
        MockDirectory.Users.firstOrNull { it.id == id }?.role == UserRole.ProcurementManager
    }.toSet()
    val warehouse = ids.filter { id ->
        MockDirectory.Users.firstOrNull { it.id == id }?.role == UserRole.WarehouseManager
    }.toSet()
    return procurement to warehouse
}

internal fun workItemNoun(kind: WorkItemKind): String = when (kind) {
    WorkItemKind.Issue -> "issue"
    WorkItemKind.PurchaseOrder -> "order"
    WorkItemKind.Task -> "task"
}

fun WorkItemRecord.toWorkItemDraft(): WorkItemDraft {
    val draft = WorkItemDraft()
    val (site, contractors) = splitAssigneeIds(assigneeIds)
    draft.stage = stage
    draft.customStage = customStage
    draft.task = task
    draft.villa = villa
    draft.floor = floor
    draft.tower = tower
    draft.apartmentUnit = apartmentUnit
    draft.dateRange = dateRange
    draft.siteEngineerIds = site
    draft.contractorIds = contractors
    draft.checklistTitle = checklistTitle
    draft.materialLines.clear()
    val lines = materials.filter(::orbitMaterialUsageLineComplete)
    if (lines.isEmpty()) {
        draft.materialLines += OrbitMaterialUsageLine(id = "m0")
    } else {
        draft.materialLines.addAll(lines)
    }
    draft.checklistItems.addAll(checklistItems)
    return draft
}

fun WorkItemRecord.toRaiseIssueDraft(): RaiseIssueDraft {
    val draft = RaiseIssueDraft()
    val work = toWorkItemDraft()
    draft.work.stage = work.stage
    draft.work.customStage = work.customStage
    draft.work.task = work.task
    draft.work.villa = work.villa
    draft.work.floor = work.floor
    draft.work.tower = work.tower
    draft.work.apartmentUnit = work.apartmentUnit
    draft.work.dateRange = work.dateRange
    draft.work.siteEngineerIds = work.siteEngineerIds
    draft.work.contractorIds = work.contractorIds
    draft.work.checklistTitle = work.checklistTitle
    draft.work.materialLines.clear()
    draft.work.materialLines.addAll(work.materialLines)
    draft.work.checklistItems.addAll(work.checklistItems)
    draft.severity = severity
    draft.description = description
    draft.photos.addAll(photos.map { DraftPhoto(it.id, it.fileName, it.fileSize) })
    return draft
}

fun WorkItemRecord.applyWorkDraft(draft: WorkItemDraft): WorkItemRecord {
    val total = draft.checklistItems.size
    val checked = draft.checklistItems.count { it.checked }
    return copy(
        stage = draft.stage,
        customStage = draft.customStage,
        task = draft.task,
        villa = draft.villa,
        floor = draft.floor,
        tower = draft.tower,
        apartmentUnit = draft.apartmentUnit,
        dateRange = draft.dateRange,
        assigneeIds = draft.siteEngineerIds + draft.contractorIds,
        materials = draft.materialLines.filter(::orbitMaterialUsageLineComplete),
        checklistTitle = draft.checklistTitle.trim().ifBlank { "Checklist" },
        checklistItems = draft.checklistItems.toList(),
        progress = if (total == 0) progress else checked / total.toFloat(),
    )
}

fun WorkItemRecord.applyIssueDraft(draft: RaiseIssueDraft): WorkItemRecord =
    applyWorkDraft(draft.work).copy(
        severity = draft.severity,
        description = draft.description.trim(),
        photos = draft.photos.map { WorkItemPhoto(it.id, it.fileName, it.fileSize) },
    )

fun WorkItemRecord.toMaterialsOrderDraft(): MaterialsOrderDraft {
    val draft = MaterialsOrderDraft()
    val (procurement, warehouse) = splitSupplyAssigneeIds(assigneeIds)
    draft.materialLines.clear()
    val lines = materials.filter(::orbitMaterialUsageLineComplete).take(1)
    if (lines.isEmpty()) {
        draft.materialLines += OrbitMaterialUsageLine(id = "m0")
    } else {
        draft.materialLines.addAll(lines)
    }
    draft.projectName = projectName
    draft.dateRange = dateRange
    draft.procurementIds = procurement
    draft.warehouseIds = warehouse
    return draft
}

fun WorkItemRecord.applyMaterialsOrderDraft(draft: MaterialsOrderDraft): WorkItemRecord {
    val lines = draft.completeMaterialLines()
    return copy(
        projectName = draft.projectName,
        villa = null,
        floor = null,
        tower = null,
        apartmentUnit = null,
        dateRange = draft.dateRange,
        assigneeIds = draft.procurementIds + draft.warehouseIds,
        materials = lines,
        task = lines.firstOrNull()?.material,
    )
}

fun WorkItemRecord.withInboxOpened(): WorkItemRecord = this

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
        materials = materialLines.filter(::orbitMaterialUsageLineComplete),
        checklistTitle = checklistTitle.trim().ifBlank { "Checklist" },
        checklistItems = checklistItems.toList(),
    )
}

fun RaiseIssueDraft.toRecord(projectType: ProjectType): WorkItemRecord =
    work.toRecord(
        kind = WorkItemKind.Issue,
        projectType = projectType,
        severity = severity,
    ).copy(
        description = description.trim(),
        photos = photos.map { WorkItemPhoto(it.id, it.fileName, it.fileSize) },
    )

fun MaterialsOrderDraft.toRecord(projectType: ProjectType): WorkItemRecord =
    WorkItemRecord(
        id = "w${Random.nextLong()}",
        number = nextWorkItemNumber(WorkItemKind.PurchaseOrder),
        kind = WorkItemKind.PurchaseOrder,
        projectType = projectType,
        stage = null,
        customStage = null,
        task = completeMaterialLines().firstOrNull()?.material,
        villa = null,
        floor = null,
        tower = null,
        apartmentUnit = null,
        projectName = projectName,
        dateRange = dateRange,
        assigneeIds = procurementIds + warehouseIds,
        progress = 0.15f,
        progressDelta = 0f,
        severity = null,
        status = WorkStatus.Open,
        materials = completeMaterialLines(),
    )

fun WorkItemRecord.withComment(comment: WorkItemComment): WorkItemRecord =
    copy(comments = comments + comment)

fun WorkItemRecord.withUsedMaterials(lines: List<OrbitMaterialUsageLine>): WorkItemRecord =
    copy(usedMaterials = lines)

fun WorkItemRecord.withLabourUsed(count: Int): WorkItemRecord =
    copy(labourUsed = count.coerceAtLeast(0))

fun WorkItemRecord.withDescription(value: String): WorkItemRecord =
    copy(description = value)

fun WorkItemRecord.withPhotos(next: List<WorkItemPhoto>): WorkItemRecord =
    copy(photos = next)

fun WorkItemRecord.withOrderPhoto(photo: WorkItemPhoto): WorkItemRecord {
    val next = copy(photos = photos + photo)
    return if (next.status == WorkStatus.Open) next.withStatus(WorkStatus.InProgress) else next
}

fun WorkItemRecord.withRework(note: String): WorkItemRecord =
    withStatus(WorkStatus.Rework).copy(reworkNote = note.trim())

fun WorkItemRecord.withRequestedMaterials(
    next: List<WorkItemMaterialRequest>,
): WorkItemRecord = copy(requestedMaterials = next)

fun WorkItemRecord.upsertRequestedMaterial(
    line: OrbitMaterialUsageLine,
): WorkItemRecord {
    if (!orbitMaterialUsageLineComplete(line)) return this
    val request = WorkItemMaterialRequest(
        id = line.id,
        material = line.material.orEmpty().trim(),
        quantity = line.quantity,
        unit = line.unit.orEmpty().trim(),
        status = requestedMaterials.firstOrNull { it.id == line.id }?.status
            ?: MaterialRequestStatus.Ordered,
    )
    val index = requestedMaterials.indexOfFirst { it.id == line.id }
    val next = if (index < 0) {
        requestedMaterials + request
    } else {
        requestedMaterials.mapIndexed { i, item -> if (i == index) request else item }
    }
    return copy(requestedMaterials = next)
}

fun WorkItemRecord.removeRequestedMaterial(id: String): WorkItemRecord =
    copy(requestedMaterials = requestedMaterials.filterNot { it.id == id })

fun WorkItemRecord.receiveRequestedMaterial(id: String): WorkItemRecord {
    val request = requestedMaterials.firstOrNull { it.id == id } ?: return this
    return copy(
        materials = materials + OrbitMaterialUsageLine(
            id = request.id,
            material = request.material,
            quantity = request.quantity,
            unit = request.unit,
        ),
        requestedMaterials = requestedMaterials.filterNot { it.id == id },
    )
}

enum class MaterialRequestStatus {
    Ordered,
    Paused,
    LowStock,
    OutOfStock,
}

data class WorkItemMaterialRequest(
    val id: String,
    val material: String,
    val quantity: Int,
    val unit: String,
    val status: MaterialRequestStatus,
)

data class WorkItemMaterialReview(
    val given: List<OrbitMaterialUsageLine>,
    val extra: List<OrbitMaterialUsageLine>,
)

/** Markdown table for a materials list, or empty when there is nothing to show. */
fun materialLinesMarkdown(lines: List<OrbitMaterialUsageLine>): String {
    val complete = lines.filter(::orbitMaterialUsageLineComplete)
    if (complete.isEmpty()) return ""
    val rows = complete.joinToString("\n") { line ->
        val name = line.material.orEmpty().trim().ifBlank { "—" }
        val unit = line.unit.orEmpty().trim().ifBlank { "—" }
        "| $name | ${line.quantity} | $unit |"
    }
    return """
        | Material | Qty | Unit |
        | --- | --- | --- |
        $rows
    """.trimIndent()
}

/** Split the updated usage log into materials that were issued and materials added on site. */
fun WorkItemRecord.materialReview(): WorkItemMaterialReview {
    val givenKeys = materials.mapNotNull { it.material?.trim()?.lowercase() }.toSet()
    val logged = usedMaterials.filter(::orbitMaterialUsageLineComplete)
    return WorkItemMaterialReview(
        given = logged.filter { it.material.orEmpty().trim().lowercase() in givenKeys },
        extra = logged.filter { it.material.orEmpty().trim().lowercase() !in givenKeys },
    )
}

fun WorkItemRecord.withChecklistChecked(id: String, checked: Boolean): WorkItemRecord {
    val nextItems = checklistItems.map { item ->
        if (item.id == id) item.copy(checked = checked) else item
    }
    val total = nextItems.size
    val done = nextItems.count { it.checked }
    return copy(
        checklistItems = nextItems,
        progress = if (total == 0) progress else done / total.toFloat(),
    )
}

fun WorkItemRecord.withStatus(next: WorkStatus): WorkItemRecord = copy(
    status = next,
    progress = when (next) {
        WorkStatus.Completed -> 1f
        WorkStatus.InProgress -> progress.coerceAtLeast(0.15f)
        WorkStatus.InReview, WorkStatus.Inspection -> progress.coerceAtLeast(0.6f)
        WorkStatus.Rework -> progress.coerceAtLeast(0.35f)
        else -> progress
    },
)

internal fun nextWorkItemNumber(kind: WorkItemKind): String {
    val prefix = when (kind) {
        WorkItemKind.Issue -> "I"
        WorkItemKind.PurchaseOrder -> "PO"
        WorkItemKind.Task -> "T"
    }
    return "$prefix-${Random.nextInt(1000, 10000)}"
}

/** Same five waypoints as the progress gallery [OrbitStepIndicator] demos. */
internal fun workItemWorkflowIndex(status: WorkStatus): Int = when (status) {
    WorkStatus.Open -> 0
    WorkStatus.InProgress, WorkStatus.Blocked -> 1
    WorkStatus.Rework, WorkStatus.Inspection -> 2
    WorkStatus.InReview, WorkStatus.Rejected -> 3
    WorkStatus.Completed, WorkStatus.Cancelled -> 4
}

internal fun workItemWorkflowSteps(
    status: WorkStatus,
    range: OrbitDateRange? = null,
): List<OrbitStep> {
    val current = workItemWorkflowIndex(status)
    val finished = status == WorkStatus.Completed
    val dates = workItemWorkflowStepDates(range, current, finished)
    return listOf(
        OrbitStep(
            label = "Scheduled",
            statusLabel = when {
                finished || current > 0 -> "Started"
                else -> "Not started"
            },
            startedOn = dates[0].startedOn,
            endedOn = dates[0].endedOn,
        ),
        OrbitStep(
            label = "In progress",
            statusLabel = when {
                finished || current > 1 -> "Submitted"
                status == WorkStatus.Blocked -> "Blocked"
                else -> "In progress"
            },
            startedOn = dates[1].startedOn,
            endedOn = dates[1].endedOn,
        ),
        OrbitStep(
            label = "Inspection",
            statusLabel = when {
                finished || current > 2 -> "Done"
                status == WorkStatus.Rework -> "Rework"
                else -> "Inspecting"
            },
            startedOn = dates[2].startedOn,
            endedOn = dates[2].endedOn,
        ),
        OrbitStep(
            label = "Approval",
            statusLabel = when {
                finished -> "Approved"
                status == WorkStatus.Rejected -> "Rejected"
                else -> "Pending"
            },
            startedOn = dates[3].startedOn,
            endedOn = dates[3].endedOn,
        ),
        OrbitStep(
            label = "Completed",
            statusLabel = when (status) {
                WorkStatus.Completed -> "Complete"
                WorkStatus.Cancelled -> "Cancelled"
                else -> "Complete"
            },
            startedOn = dates[4].startedOn,
            endedOn = dates[4].endedOn,
        ),
    )
}

internal data class WorkItemStepDates(
    val startedOn: String?,
    val endedOn: String?,
)

internal fun workItemWorkflowStepDates(
    range: OrbitDateRange?,
    current: Int,
    finished: Boolean,
    stepCount: Int = 5,
): List<WorkItemStepDates> {
    if (range == null || stepCount <= 0) {
        return List(stepCount.coerceAtLeast(0)) { WorkItemStepDates(null, null) }
    }
    val startDay = range.start.toLocalDate().toEpochDays()
    val endDay = range.end.toLocalDate().toEpochDays()
    val span = (endDay - startDay).coerceAtLeast(0)
    val points = (0 until stepCount).map { index ->
        val day = if (stepCount == 1) startDay else startDay + span * index / (stepCount - 1)
        LocalDate.fromEpochDays(day).toOrbitCalendarDate()
    }
    return List(stepCount) { index ->
        val started = finished || index <= current
        val ended = finished || index < current
        WorkItemStepDates(
            startedOn = if (started) points[index].formatSlashed() else null,
            endedOn = if (ended) {
                (points.getOrNull(index + 1) ?: range.end).formatSlashed()
            } else {
                null
            },
        )
    }
}
