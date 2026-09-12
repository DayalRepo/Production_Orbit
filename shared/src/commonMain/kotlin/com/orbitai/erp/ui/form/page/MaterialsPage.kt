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
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitQuantityUnitField
import com.orbitai.erp.core.designsystem.component.input.orbitSuggestedUnitForMaterial
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.dropdown.ConstructionMaterials
import com.orbitai.erp.ui.component.dropdown.ConstructionUnits
import com.orbitai.erp.ui.form.FormFieldLabel

/**
 * One material, then quantity + unit. Creating a material or unit grows the local catalogues.
 */
@Composable
fun MaterialsPage(
    material: String?,
    onMaterialSelect: (String) -> Unit,
    extraMaterials: List<String>,
    onMaterialCreated: (String) -> Unit,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    unit: String?,
    onUnitSelect: (String) -> Unit,
    extraUnits: List<String>,
    onUnitCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val materials = remember(extraMaterials) { ConstructionMaterials + extraMaterials }
    val units = remember(extraUnits) { ConstructionUnits + extraUnits }
    var creatingMaterial by remember { mutableStateOf(false) }
    var creatingUnit by remember { mutableStateOf(false) }
    var duplicateMaterial by remember { mutableStateOf(false) }
    var duplicateUnit by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.fieldGap),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Material")
            OrbitDropdownField(
                selected = material,
                options = materials,
                onSelect = { chosen ->
                    onMaterialSelect(chosen)
                    if (unit == null) {
                        orbitSuggestedUnitForMaterial(chosen)?.let(onUnitSelect)
                    }
                },
                label = "Material",
                placeholder = "Select material",
                searchPlaceholder = "Search materials",
                addLabel = "Add material",
                onAddRequest = {
                    duplicateMaterial = false
                    creatingMaterial = true
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
            FormFieldLabel("Quantity and unit")
            OrbitQuantityUnitField(
                value = quantity,
                onValueChange = onQuantityChange,
                selectedUnit = unit,
                units = units,
                onUnitSelect = onUnitSelect,
                quantityLabel = "Quantity",
                unitLabel = "Unit",
                modifier = Modifier.fillMaxWidth(),
                onAddUnitRequest = {
                    duplicateUnit = false
                    creatingUnit = true
                },
            )
        }
    }

    if (creatingMaterial) {
        OrbitCreateDialog(
            title = "Add material",
            info = "Include the grade or size — that is what gets ordered.",
            label = "Material name",
            placeholder = "e.g. Cement (OPC 43)",
            state = if (duplicateMaterial) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = materials.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    onMaterialSelect(existing)
                    duplicateMaterial = true
                    creatingMaterial = false
                } else {
                    onMaterialCreated(name)
                    onMaterialSelect(name)
                    orbitSuggestedUnitForMaterial(name)?.let(onUnitSelect)
                    creatingMaterial = false
                }
            },
            onDismiss = { creatingMaterial = false },
        )
    }

    if (creatingUnit) {
        OrbitCreateDialog(
            title = "Add unit",
            info = "Shared across the project. Use the unit the store issues against.",
            label = "Unit name",
            placeholder = "e.g. Bundles",
            state = if (duplicateUnit) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = units.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    onUnitSelect(existing)
                    duplicateUnit = true
                    creatingUnit = false
                } else {
                    onUnitCreated(name)
                    onUnitSelect(name)
                    creatingUnit = false
                }
            },
            onDismiss = { creatingUnit = false },
        )
    }
}
