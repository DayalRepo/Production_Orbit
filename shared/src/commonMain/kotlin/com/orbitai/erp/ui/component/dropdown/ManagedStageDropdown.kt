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
 * A single-select dropdown that can also grow its own vocabulary.
 *
 * ### Why the create flow is here and not in the design system
 *
 * [OrbitDropdownField] raises "the user asked to add one" and stops. Everything after that is
 * application knowledge: whether the name collides with a stage that already exists, where the new
 * stage is inserted, and — once there is a backend — whether the server accepted it and what the
 * canonical spelling came back as. A design system component that owned any of that would be
 * carrying a project's data rules inside a reusable library, which is exactly what stops it being
 * reusable.
 *
 * So the split is: the library draws the field, the panel, the add row and the dialog; this owns the
 * list. Swapping [onCreate] for a repository call is the entire change needed to persist these, and
 * no component in `:core:designsystem` has to know it happened.
 *
 * ### Duplicates are refused rather than silently merged
 *
 * A near-duplicate stage is the specific failure this flow invites — the user searched, missed the
 * existing entry, and is now naming it again in their own words. Matching case-insensitively and
 * refusing with an error state on the field is cheap, and it selects the existing stage instead so
 * the user still ends up where they were going rather than being told off and left with nothing.
 *
 * @param onCreate called with the trimmed, accepted name once it has passed the duplicate check.
 *   The hook for a backend write; the local list is updated regardless so the UI stays responsive.
 */
@Composable
fun ManagedStageDropdown(
    label: String,
    modifier: Modifier = Modifier,
    initialStages: List<String> = WorkSequence.allStages,
    placeholder: String = "Select a stage",
    onCreate: (String) -> Unit = {},
) {
    var stages by remember { mutableStateOf(initialStages) }
    var selected by remember { mutableStateOf<String?>(null) }
    var creating by remember { mutableStateOf(false) }
    var duplicate by remember { mutableStateOf(false) }

    OrbitDropdownField(
        selected = selected,
        options = stages,
        onSelect = { selected = it },
        label = label,
        placeholder = placeholder,
        searchPlaceholder = "Search stages",
        addLabel = "Add stage",
        onAddRequest = {
            duplicate = false
            creating = true
        },
        modifier = modifier,
    )

    if (creating) {
        OrbitCreateDialog(
            title = "Add stage",
            // Short, and aimed squarely at the mistake this dialog invites. The user is one tap from
            // creating a stage that already exists under different wording, and the only thing that
            // stops them is knowing the list is shared and that the sequence document is the
            // authority on spelling.
            info = "Shared across the project. Name it as the work sequence does.",
            label = "Stage name",
            placeholder = "e.g. Slab Concreting",
            state = if (duplicate) OrbitFieldState.Error else OrbitFieldState.Default,
            onCreate = { name ->
                val existing = stages.firstOrNull { it.equals(name, ignoreCase = true) }
                if (existing != null) {
                    // Selecting the one that already exists, rather than only complaining. The user
                    // wanted this stage on the form; the fact that they spelled it differently is
                    // not a reason to send them back to an empty dialog.
                    selected = existing
                    duplicate = true
                    creating = false
                } else {
                    stages = stages + name
                    selected = name
                    onCreate(name)
                    creating = false
                }
            },
            onDismiss = { creating = false },
        )
    }
}
