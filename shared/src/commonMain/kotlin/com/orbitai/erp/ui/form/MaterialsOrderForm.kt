package com.orbitai.erp.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.data.session.MockDirectory
import com.orbitai.erp.core.designsystem.component.dialog.OrbitConfirmDialog
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.model.ProjectType
import com.orbitai.erp.ui.card.WorkItemRecord
import com.orbitai.erp.ui.card.toMaterialsOrderDraft
import com.orbitai.erp.ui.component.button.ActionKind
import com.orbitai.erp.ui.form.page.AssignSupplyPage
import com.orbitai.erp.ui.form.page.MaterialsPage
import com.orbitai.erp.ui.form.page.OrderProjectSchedulePage

@Composable
fun MaterialsOrderForm(
    projectType: ProjectType,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    editing: WorkItemRecord? = null,
    onCreate: (MaterialsOrderDraft) -> Unit = {},
) {
    val draft = remember(editing?.id) {
        editing?.toMaterialsOrderDraft() ?: MaterialsOrderDraft()
    }
    var confirmOpen by remember { mutableStateOf(false) }
    val projects = remember(projectType) {
        MockDirectory.OrganisationProjects.filter { it.type == projectType }.map { it.name }
    }
    LaunchedEffect(projects) {
        if (draft.projectName == null && projects.size == 1) {
            draft.projectName = projects.first()
        }
    }

    WizardScaffold(
        title = if (editing != null) "Edit order" else "Materials order",
        subtitle = projectType.displayName,
        dismiss = ActionKind.Cancel,
        confirm = if (editing != null) ActionKind.Update else ActionKind.Confirm,
        onDismiss = onDismiss,
        onConfirm = {
            if (editing != null) onCreate(draft) else confirmOpen = true
        },
        modifier = modifier,
    ) {
        FormSection(showDivider = false) {
            MaterialsPage(
                lines = draft.materialLines.take(1),
                extraMaterials = draft.extraMaterials,
                extraUnits = draft.extraUnits,
                allowMultipleLines = false,
                onMaterialSelect = { id, material ->
                    draft.replaceMaterialLine(id) { it.copy(material = material) }
                },
                onQuantityChange = { id, quantity ->
                    draft.replaceMaterialLine(id) { it.copy(quantity = quantity) }
                },
                onUnitSelect = { id, unit ->
                    draft.replaceMaterialLine(id) { it.copy(unit = unit) }
                },
                onAddLine = {},
                onRemoveLine = { id ->
                    draft.materialLines[0] = OrbitMaterialUsageLine(id = id)
                },
                onMaterialCreated = { draft.extraMaterials = draft.extraMaterials + it },
                onUnitCreated = { draft.extraUnits = draft.extraUnits + it },
            )
        }
        FormSection {
            OrderProjectSchedulePage(
                projects = projects,
                projectName = draft.projectName,
                onProjectSelect = { draft.projectName = it },
                dateRange = draft.dateRange,
                onDateRangeChange = { draft.dateRange = it },
            )
        }
        FormSection {
            AssignSupplyPage(
                procurementIds = draft.procurementIds,
                onProcurementToggle = { id ->
                    draft.procurementIds = draft.procurementIds.toggle(id)
                },
                warehouseIds = draft.warehouseIds,
                onWarehouseToggle = { id ->
                    draft.warehouseIds = draft.warehouseIds.toggle(id)
                },
            )
        }
    }

    if (confirmOpen) {
        OrbitConfirmDialog(
            title = "Place order",
            message = "Place this materials order?",
            confirmLabel = "Yes",
            dismissLabel = "No",
            onConfirm = {
                confirmOpen = false
                onCreate(draft)
            },
            onDismiss = { confirmOpen = false },
        )
    }
}
