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
 * A units picker for material quantities, with search and the ability to add custom units.
 *
 * Same split as [ManagedStageDropdown]: the design system draws the field and dialog; this owns the
 * catalogue of units and what happens when the user adds one.
 */
@Composable
fun ManagedUnitsDropdown(
    label: String,
    modifier: Modifier = Modifier,
    initialUnits: List<String> = ConstructionUnits,
    placeholder: String = "Select unit",
    onCreate: (String) -> Unit = {},
) {
    var units by remember { mutableStateOf(initialUnits) }
    var selected by remember { mutableStateOf<String?>(null) }
    var creating by remember { mutableStateOf(false) }
    var duplicate by remember { mutableStateOf(false) }

    OrbitDropdownField(
        selected = selected,
        options = units,
        onSelect = { selected = it },
        label = label,
        placeholder = placeholder,
        searchPlaceholder = "Search units",
        addLabel = "Add unit",
        onAddRequest = {
            duplicate = false
            creating = true
        },
        modifier = modifier,
    )

    if (creating) {
        OrbitCreateDialog(
            title = "Add unit",
            info = "Shared across materials on this project. Use the abbreviation your team orders by.",
            label = "Unit name",
            placeholder = "e.g. Bags",
            state = if (duplicate) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = units.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    selected = existing
                    duplicate = true
                    creating = false
                } else {
                    units = units + name
                    selected = name
                    onCreate(name)
                    creating = false
                }
            },
            onDismiss = { creating = false },
        )
    }
}
