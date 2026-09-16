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
 *
 * Pass [selected] + [onSelect] for controlled use (invoice lines); omit them for standalone demos.
 */
@Composable
fun ManagedUnitsDropdown(
    label: String,
    modifier: Modifier = Modifier,
    selected: String? = null,
    onSelect: ((String) -> Unit)? = null,
    initialUnits: List<String> = ConstructionUnits,
    placeholder: String = "Select unit",
    onCreate: (String) -> Unit = {},
) {
    var units by remember { mutableStateOf(initialUnits) }
    var internalSelected by remember { mutableStateOf<String?>(null) }
    var creating by remember { mutableStateOf(false) }
    var duplicate by remember { mutableStateOf(false) }
    val current = selected ?: internalSelected
    val merged = remember(units, current) {
        if (current.isNullOrBlank() || units.any { it.equals(current, ignoreCase = true) }) {
            units
        } else {
            units + current
        }
    }
    fun choose(value: String) {
        if (onSelect != null) onSelect(value) else internalSelected = value
    }

    OrbitDropdownField(
        selected = current,
        options = merged,
        onSelect = ::choose,
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
                    choose(existing)
                    duplicate = true
                    creating = false
                } else {
                    units = units + name
                    choose(name)
                    onCreate(name)
                    creating = false
                }
            },
            onDismiss = { creating = false },
        )
    }
}
