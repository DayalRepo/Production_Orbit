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

private val DefaultInvoiceLineDescriptions = listOf(
    "Slab concreting",
    "Formwork labour",
    "Blockwork",
    "Internal plaster",
    "Electrical rough-in",
    "Plumbing rough-in",
    "Flooring",
    "Painting",
    "Scaffolding hire",
    "Site supervision",
)

/**
 * Line-item description picker with create-new, matching [ManagedStageDropdown].
 */
@Composable
fun ManagedInvoiceLineDropdown(
    selected: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Description",
    placeholder: String = "Select description",
    initialOptions: List<String> = DefaultInvoiceLineDescriptions,
    onCreate: (String) -> Unit = {},
) {
    var options by remember { mutableStateOf(initialOptions) }
    var creating by remember { mutableStateOf(false) }
    var duplicate by remember { mutableStateOf(false) }

    // Keep selected value visible even if it is not yet in the catalogue (typed previously).
    val merged = remember(options, selected) {
        if (selected.isNullOrBlank() || options.any { it.equals(selected, ignoreCase = true) }) {
            options
        } else {
            options + selected
        }
    }

    OrbitDropdownField(
        selected = selected?.takeIf { it.isNotBlank() },
        options = merged,
        onSelect = onSelect,
        label = label,
        placeholder = placeholder,
        searchPlaceholder = "Search descriptions",
        addLabel = "Add description",
        onAddRequest = {
            duplicate = false
            creating = true
        },
        modifier = modifier,
    )

    if (creating) {
        OrbitCreateDialog(
            title = "Add line description",
            info = "Shared across invoices on this project.",
            label = "Description",
            placeholder = "e.g. Slab concreting",
            state = if (duplicate) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = options.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    onSelect(existing)
                    duplicate = true
                    creating = false
                } else {
                    options = options + name
                    onSelect(name)
                    onCreate(name)
                    creating = false
                }
            },
            onDismiss = { creating = false },
        )
    }
}
