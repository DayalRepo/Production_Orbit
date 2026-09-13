package com.orbitai.erp.ui.form.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.dialog.OrbitCreateDialog
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLine
import com.orbitai.erp.core.designsystem.component.input.OrbitMaterialUsageLog
import com.orbitai.erp.core.designsystem.component.input.orbitSuggestedUnitForMaterial
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.dropdown.ConstructionMaterials
import com.orbitai.erp.ui.component.dropdown.ConstructionUnits
import com.orbitai.erp.ui.form.FormFieldLabel

/**
 * Several material lines, each with its own quantity and unit.
 *
 * Creating a material or unit grows the local catalogues and fills the line that asked.
 */
@Composable
fun MaterialsPage(
    lines: List<OrbitMaterialUsageLine>,
    extraMaterials: List<String>,
    extraUnits: List<String>,
    onMaterialSelect: (id: String, material: String) -> Unit,
    onQuantityChange: (id: String, quantity: Int) -> Unit,
    onUnitSelect: (id: String, unit: String) -> Unit,
    onAddLine: () -> Unit,
    onRemoveLine: (id: String) -> Unit,
    onMaterialCreated: (String) -> Unit,
    onUnitCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Materials",
    allowMultipleLines: Boolean = true,
) {
    val materials = remember(extraMaterials) { ConstructionMaterials + extraMaterials }
    val units = remember(extraUnits) { ConstructionUnits + extraUnits }
    var creatingMaterialFor by remember { mutableStateOf<String?>(null) }
    var creatingUnitFor by remember { mutableStateOf<String?>(null) }
    var duplicateMaterial by remember { mutableStateOf(false) }
    var duplicateUnit by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(OrbitTheme.spacing.sm),
    ) {
        if (label.isNotBlank()) {
            FormFieldLabel(label)
        }
        OrbitMaterialUsageLog(
            lines = lines,
            materials = materials,
            units = units,
            onMaterialSelect = onMaterialSelect,
            onQuantityChange = onQuantityChange,
            onUnitSelect = onUnitSelect,
            onAdd = if (allowMultipleLines) onAddLine else null,
            onRemove = onRemoveLine,
            modifier = Modifier.fillMaxWidth(),
            showTitle = false,
            addMaterialLabel = "Add material",
            onAddMaterialRequest = { lineId ->
                duplicateMaterial = false
                creatingMaterialFor = lineId
            },
            onAddUnitRequest = { lineId ->
                duplicateUnit = false
                creatingUnitFor = lineId
            },
        )
    }

    val creatingMaterialId = creatingMaterialFor
    if (creatingMaterialId != null) {
        OrbitCreateDialog(
            title = "Add material",
            info = "Include the grade or size — that is what gets ordered.",
            label = "Material name",
            placeholder = "e.g. Cement (OPC 43)",
            state = if (duplicateMaterial) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = materials.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    onMaterialSelect(creatingMaterialId, existing)
                    duplicateMaterial = true
                    creatingMaterialFor = null
                } else {
                    onMaterialCreated(name)
                    onMaterialSelect(creatingMaterialId, name)
                    orbitSuggestedUnitForMaterial(name)?.let { onUnitSelect(creatingMaterialId, it) }
                    creatingMaterialFor = null
                }
            },
            onDismiss = { creatingMaterialFor = null },
        )
    }

    val creatingUnitId = creatingUnitFor
    if (creatingUnitId != null) {
        OrbitCreateDialog(
            title = "Add unit",
            info = "Shared across the project. Use the unit the store issues against.",
            label = "Unit name",
            placeholder = "e.g. Bundles",
            state = if (duplicateUnit) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = units.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    onUnitSelect(creatingUnitId, existing)
                    duplicateUnit = true
                    creatingUnitFor = null
                } else {
                    onUnitCreated(name)
                    onUnitSelect(creatingUnitId, name)
                    creatingUnitFor = null
                }
            },
            onDismiss = { creatingUnitFor = null },
        )
    }
}
