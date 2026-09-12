package com.orbitai.erp.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.display.OrbitChecklistItem
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.core.model.Severity
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
    onCreate: (WorkItemDraft) -> Unit = {},
) {
    val draft = remember { CreateTaskDraft() }.work

    WizardScaffold(
        title = "Create task",
        subtitle = projectType.displayName,
        dismiss = ActionKind.Cancel,
        confirm = ActionKind.Create,
        onDismiss = onDismiss,
        onConfirm = { onCreate(draft) },
        modifier = modifier,
    ) {
        FormSection("Stage and task", showDivider = false) {
            StageAndTaskFields(draft = draft, projectType = projectType)
        }
        FormSection("Materials") {
            MaterialsPage(
                material = draft.material,
                onMaterialSelect = { draft.material = it },
                extraMaterials = draft.extraMaterials,
                onMaterialCreated = { draft.extraMaterials = draft.extraMaterials + it },
                quantity = draft.quantity,
                onQuantityChange = { draft.quantity = it },
                unit = draft.materialUnit,
                onUnitSelect = { draft.materialUnit = it },
                extraUnits = draft.extraUnits,
                onUnitCreated = { draft.extraUnits = draft.extraUnits + it },
            )
        }
        FormSection("Checklist") {
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
        FormSection("Location") {
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
        FormSection("Assigned") {
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
