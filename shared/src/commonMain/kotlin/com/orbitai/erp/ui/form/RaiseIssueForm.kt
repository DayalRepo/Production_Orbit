package com.orbitai.erp.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.form.page.AssignPage
import com.orbitai.erp.ui.form.page.ChecklistPage
import com.orbitai.erp.ui.form.page.IssuePhotosPage
import com.orbitai.erp.ui.form.page.LocationSchedulePage
import com.orbitai.erp.ui.form.page.MaterialsPage

@Composable
fun RaiseIssueForm(
    projectType: ProjectType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onRaise: (RaiseIssueDraft) -> Unit = {},
) {
    val draft = remember { RaiseIssueDraft() }
    val work = draft.work

    WizardScaffold(
        title = "Raise issue",
        subtitle = projectType.displayName,
        dismiss = ActionKind.Cancel,
        confirm = ActionKind.Raise,
        onDismiss = onDismiss,
        onConfirm = { onRaise(draft) },
        modifier = modifier,
    ) {
        FormSection("Stage and task", showDivider = false) {
            StageAndTaskFields(
                draft = work,
                projectType = projectType,
                severity = draft.severity,
                onSeveritySelect = { draft.severity = it },
            )
        }
        FormSection("Photos") {
            IssuePhotosPage()
        }
        FormSection("Materials") {
            MaterialsPage(
                material = work.material,
                onMaterialSelect = { work.material = it },
                extraMaterials = work.extraMaterials,
                onMaterialCreated = { work.extraMaterials = work.extraMaterials + it },
                quantity = work.quantity,
                onQuantityChange = { work.quantity = it },
                unit = work.materialUnit,
                onUnitSelect = { work.materialUnit = it },
                extraUnits = work.extraUnits,
                onUnitCreated = { work.extraUnits = work.extraUnits + it },
            )
        }
        FormSection("Checklist") {
            ChecklistPage(
                title = work.checklistTitle,
                onTitleChange = { work.checklistTitle = it },
                items = work.checklistItems.toList(),
                draft = work.checklistDraft,
                onDraftChange = { work.checklistDraft = it },
                onAddItem = { addChecklistItem(work) },
                onRemoveItem = { id -> work.checklistItems.removeAll { it.id == id } },
            )
        }
        FormSection("Location") {
            LocationSchedulePage(
                projectType = projectType,
                villa = work.villa,
                onVillaSelect = { work.villa = it },
                floor = work.floor,
                onFloorSelect = { work.floor = it },
                tower = work.tower,
                onTowerSelect = { next ->
                    if (work.tower != next) {
                        work.tower = next
                        work.floor = null
                        work.apartmentUnit = null
                    }
                },
                apartmentUnit = work.apartmentUnit,
                onApartmentUnitSelect = { work.apartmentUnit = it },
                dateRange = work.dateRange,
                onDateRangeChange = { work.dateRange = it },
            )
        }
        FormSection("Assigned") {
            AssignPage(
                siteEngineerIds = work.siteEngineerIds,
                onSiteEngineerToggle = { id ->
                    work.siteEngineerIds = work.siteEngineerIds.toggle(id)
                },
                contractorIds = work.contractorIds,
                onContractorToggle = { id ->
                    work.contractorIds = work.contractorIds.toggle(id)
                },
            )
        }
    }
}
