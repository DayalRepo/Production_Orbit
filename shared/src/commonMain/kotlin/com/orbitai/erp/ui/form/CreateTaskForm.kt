package com.orbitai.erp.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.Severity
import com.orbitai.erp.ui.card.WorkItemRecord
import com.orbitai.erp.ui.card.toWorkItemDraft
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.form.page.AssignPage
import com.orbitai.erp.ui.form.page.ChecklistPage
import com.orbitai.erp.ui.form.page.LocationSchedulePage
import com.orbitai.erp.ui.form.page.MaterialsPage
import com.orbitai.erp.ui.form.page.StageTaskPage
import kotlin.random.Random

@Composable
fun CreateTaskForm(
    projectType: ProjectType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    editing: WorkItemRecord? = null,
    onCreate: (WorkItemDraft) -> Unit = {},
) {
    val draft = remember(editing?.id) {
        editing?.toWorkItemDraft() ?: CreateTaskDraft().work
    }

    WizardScaffold(
        title = if (editing != null) "Edit task" else "Create task",
        subtitle = projectType.displayName,
        dismiss = ActionKind.Cancel,
        confirm = if (editing != null) ActionKind.Update else ActionKind.Create,
        onDismiss = onDismiss,
        onConfirm = { onCreate(draft) },
        modifier = modifier,
    ) {
        FormSection(showDivider = false) {
            StageAndTaskFields(draft = draft, projectType = projectType)
        }
        FormSection {
            MaterialsPage(
                lines = draft.materialLines.toList(),
                extraMaterials = draft.extraMaterials,
                extraUnits = draft.extraUnits,
                onMaterialSelect = { id, material ->
                    draft.replaceMaterialLine(id) { it.copy(material = material) }
                },
                onQuantityChange = { id, quantity ->
                    draft.replaceMaterialLine(id) { it.copy(quantity = quantity) }
                },
                onUnitSelect = { id, unit ->
                    draft.replaceMaterialLine(id) { it.copy(unit = unit) }
                },
                onAddLine = {
                    draft.materialLines += OrbitMaterialUsageLine(id = "m${Random.nextLong()}")
                },
                onRemoveLine = { id ->
                    if (draft.materialLines.size == 1) {
                        draft.materialLines[0] = OrbitMaterialUsageLine(id = draft.materialLines[0].id)
                    } else {
                        draft.materialLines.removeAll { it.id == id }
                    }
                },
                onMaterialCreated = { draft.extraMaterials = draft.extraMaterials + it },
                onUnitCreated = { draft.extraUnits = draft.extraUnits + it },
            )
        }
        FormSection {
            ChecklistPage(
                title = draft.checklistTitle,
                onTitleChange = { draft.checklistTitle = it },
                items = draft.checklistItems.toList(),
                draft = draft.checklistDraft,
                onDraftChange = { draft.checklistDraft = it },
                onAddItem = { addChecklistItem(draft) },
                onRemoveItem = { id -> draft.checklistItems.removeAll { it.id == id } },
            )
        }
        FormSection {
            LocationSchedulePage(
                projectType = projectType,
                villa = draft.villa,
                onVillaSelect = { draft.villa = it },
                floor = draft.floor,
                onFloorSelect = { draft.floor = it },
                tower = draft.tower,
                onTowerSelect = { next ->
                    if (draft.tower != next) {
                        draft.tower = next
                        draft.floor = null
                        draft.apartmentUnit = null
                    }
                },
                apartmentUnit = draft.apartmentUnit,
                onApartmentUnitSelect = { draft.apartmentUnit = it },
                dateRange = draft.dateRange,
                onDateRangeChange = { draft.dateRange = it },
            )
        }
        FormSection {
            AssignPage(
                siteEngineerIds = draft.siteEngineerIds,
                onSiteEngineerToggle = { id ->
                    draft.siteEngineerIds = draft.siteEngineerIds.toggle(id)
                },
                contractorIds = draft.contractorIds,
                onContractorToggle = { id ->
                    draft.contractorIds = draft.contractorIds.toggle(id)
                },
            )
        }
    }
}

@Composable
internal fun StageAndTaskFields(
    draft: WorkItemDraft,
    projectType: ProjectType,
    severity: Severity? = null,
    onSeveritySelect: ((Severity) -> Unit)? = null,
) {
    StageTaskPage(
        projectType = projectType,
        stage = draft.stage,
        onStageSelect = { next ->
            if (draft.stage != next || draft.customStage != null) {
                draft.stage = next
                draft.customStage = null
                draft.task = null
            }
        },
        customStage = draft.customStage,
        extraStages = draft.extraStages,
        onCustomStageSelect = { label ->
            if (draft.customStage != label) {
                draft.customStage = label
                draft.stage = null
                draft.task = null
            }
        },
        onStageCreated = { name ->
            draft.extraStages = draft.extraStages + name
            draft.customStage = name
            draft.stage = null
            draft.task = null
        },
        task = draft.task,
        onTaskSelect = { draft.task = it },
        extraTasks = draft.extraTasks,
        onTaskCreated = { name ->
            draft.extraTasks = draft.extraTasks + name
            draft.task = name
        },
        severity = severity,
        onSeveritySelect = onSeveritySelect,
    )
}

internal fun addChecklistItem(draft: WorkItemDraft) {
    val label = draft.checklistDraft.trim()
    if (label.isEmpty()) return
    draft.checklistItems += OrbitChecklistItem(
        id = "n${Random.nextLong()}",
        label = label,
    )
    draft.checklistDraft = ""
}

internal fun Set<String>.toggle(id: String): Set<String> =
    if (id in this) this - id else this + id
