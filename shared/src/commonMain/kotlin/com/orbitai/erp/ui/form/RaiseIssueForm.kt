package com.orbitai.erp.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.card.WorkItemRecord
import com.orbitai.erp.ui.card.toRaiseIssueDraft
import com.orbitai.erp.ui.component.button.ActionKind
import kotlin.random.Random
import com.orbitai.erp.ui.form.page.AssignPage
import com.orbitai.erp.ui.form.page.ChecklistPage
import com.orbitai.erp.ui.form.page.IssuePhotosPage
import com.orbitai.erp.ui.form.page.LocationSchedulePage
import com.orbitai.erp.ui.form.page.MaterialsPage
import com.orbitai.erp.ui.form.page.SeverityPicker

@Composable
fun RaiseIssueForm(
    projectType: ProjectType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    editing: WorkItemRecord? = null,
    onRaise: (RaiseIssueDraft) -> Unit = {},
) {
    val draft = remember(editing?.id) {
        editing?.toRaiseIssueDraft() ?: RaiseIssueDraft()
    }
    val work = draft.work

    WizardScaffold(
        title = if (editing != null) "Edit issue" else "Raise issue",
        subtitle = projectType.displayName,
        dismiss = ActionKind.Cancel,
        confirm = if (editing != null) ActionKind.Update else ActionKind.Raise,
        onDismiss = onDismiss,
        onConfirm = { onRaise(draft) },
        modifier = modifier,
    ) {
        FormSection(showDivider = false) {
            SeverityPicker(
                selected = draft.severity,
                onSelect = { draft.severity = it },
            )
        }
        FormSection {
            IssuePhotosPage(
                description = draft.description,
                initialPhotos = draft.photos.toList(),
                onDescriptionChange = { draft.description = it },
                onPhotoCompleted = { id, name, size ->
                    if (draft.photos.none { it.id == id }) {
                        draft.photos += DraftPhoto(id, name, size)
                    }
                },
                onPhotoRemoved = { id ->
                    draft.photos.removeAll { it.id == id }
                },
            )
        }
        FormSection {
            MaterialsPage(
                lines = work.materialLines.toList(),
                extraMaterials = work.extraMaterials,
                extraUnits = work.extraUnits,
                onMaterialSelect = { id, material ->
                    work.replaceMaterialLine(id) { it.copy(material = material) }
                },
                onQuantityChange = { id, quantity ->
                    work.replaceMaterialLine(id) { it.copy(quantity = quantity) }
                },
                onUnitSelect = { id, unit ->
                    work.replaceMaterialLine(id) { it.copy(unit = unit) }
                },
                onAddLine = {
                    work.materialLines += OrbitMaterialUsageLine(id = "m${Random.nextLong()}")
                },
                onRemoveLine = { id ->
                    if (work.materialLines.size == 1) {
                        work.materialLines[0] = OrbitMaterialUsageLine(id = work.materialLines[0].id)
                    } else {
                        work.materialLines.removeAll { it.id == id }
                    }
                },
                onMaterialCreated = { work.extraMaterials = work.extraMaterials + it },
                onUnitCreated = { work.extraUnits = work.extraUnits + it },
            )
        }
        FormSection {
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
        FormSection {
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
        FormSection {
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
