package com.orbitai.erp.ui.component.dropdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.dialog.OrbitCreateDialog
import com.orbitai.erp.core.designsystem.component.input.OrbitMultiSelectField

/**
 * A materials picker that can also grow its own catalogue.
 *
 * The same split as [ManagedStageDropdown]: the design system draws the field, the panel and the
 * dialog, and this owns the list of materials and what happens when the user adds one. See there for
 * why the create flow does not belong in the library.
 *
 * ### A newly created material is selected immediately
 *
 * Somebody who has just typed "Cement (OPC 43)" into the add dialog wants it on this requisition —
 * that is why they went looking for it. Adding it to the catalogue and leaving it unselected means
 * they have to find it again in a list of twenty and tap it a second time, which is the sort of
 * small tax that gets a form abandoned halfway through.
 *
 * @param onCreate the hook for a backend write once the catalogue is server-owned. The local list is
 *   updated either way, so the field responds immediately rather than waiting on a round trip.
 */
@Composable
fun ManagedMaterialsDropdown(
    label: String,
    modifier: Modifier = Modifier,
    initialMaterials: List<String> = ConstructionMaterials,
    initialSelection: List<String> = emptyList(),
    placeholder: String = "Add materials",
    onCreate: (String) -> Unit = {},
) {
    var catalogue by remember { mutableStateOf(initialMaterials) }
    var selected by remember { mutableStateOf(initialSelection) }
    var creating by remember { mutableStateOf(false) }

    OrbitMultiSelectField(
        selected = selected,
        options = catalogue,
        onToggle = { item ->
            selected = if (item in selected) selected - item else selected + item
        },
        label = label,
        placeholder = placeholder,
        searchPlaceholder = "Search materials",
        addLabel = "Add material",
        onAddRequest = { creating = true },
        modifier = modifier,
    )

    if (creating) {
        OrbitCreateDialog(
            title = "Add material",
            // The grade is the point. "Cement" cannot be ordered and "Cement (OPC 53)" can, and a
            // catalogue that fills up with ungraded entries produces requisitions a supplier has to
            // ring back about.
            info = "Include the grade or size — that is what gets ordered.",
            label = "Material name",
            placeholder = "e.g. Cement (OPC 43)",
            onCreate = { name ->
                val existing = catalogue.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing == null) {
                    catalogue = catalogue + name
                    onCreate(name)
                }
                // Selected either way. Whether the material was new or already in the catalogue, the
                // user's intent was to put it on this requisition.
                val resolved = existing ?: name
                if (resolved !in selected) selected = selected + resolved
                creating = false
            },
            onDismiss = { creating = false },
        )
    }
}
