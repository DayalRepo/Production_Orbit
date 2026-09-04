package com.orbitai.erp.ui.component.dropdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.dialog.OrbitCreateDialog
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState

/**
 * A single-select materials picker that can also grow its own catalogue.
 *
 * Companion to [ManagedMaterialsDropdown] (multi-select chips). Use this when the answer is one
 * material — e.g. logging what was consumed on a line — not a set for a requisition.
 *
 * Same split as [ManagedStageDropdown]: the design system draws the field; this owns the catalogue
 * and the create dialog.
 */
@Composable
fun ManagedMaterialDropdown(
    label: String,
    modifier: Modifier = Modifier,
    initialMaterials: List<String> = ConstructionMaterials,
    initialSelection: String? = null,
    placeholder: String = "Select material",
    onCreate: (String) -> Unit = {},
) {
    var catalogue by remember { mutableStateOf(initialMaterials) }
    var selected by remember { mutableStateOf(initialSelection) }
    var creating by remember { mutableStateOf(false) }
    var duplicate by remember { mutableStateOf(false) }

    OrbitDropdownField(
        selected = selected,
        options = catalogue,
        onSelect = { selected = it },
        label = label,
        placeholder = placeholder,
        searchPlaceholder = "Search materials",
        addLabel = "Add material",
        onAddRequest = {
            duplicate = false
            creating = true
        },
        modifier = modifier,
    )

    if (creating) {
        OrbitCreateDialog(
            title = "Add material",
            info = "Include the grade or size — that is what gets ordered.",
            label = "Material name",
            placeholder = "e.g. Cement (OPC 43)",
            state = if (duplicate) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = catalogue.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    selected = existing
                    duplicate = true
                    creating = false
                } else {
                    catalogue = catalogue + name
                    selected = name
                    onCreate(name)
                    creating = false
                }
            },
            onDismiss = { creating = false },
        )
    }
}
