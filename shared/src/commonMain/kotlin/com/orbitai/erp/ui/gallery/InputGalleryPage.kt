package com.orbitai.erp.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldSize
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.input.OrbitQuantityField
import com.orbitai.erp.core.designsystem.component.input.OrbitSearchField
import com.orbitai.erp.core.designsystem.component.input.OrbitTextField
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.ui.component.dropdown.ManagedMaterialsDropdown
import com.orbitai.erp.ui.component.dropdown.ManagedStageDropdown

/**
 * Text and search fields.
 *
 * These need a device rather than a screenshot more than anything else in the gallery. Focus,
 * caret, selection handles and the placeholder's disappearance are all behaviours, and the two
 * things most likely to be wrong — the rim animating on focus, and the keyboard offering a Search
 * key instead of a newline — do not appear in a still image at all.
 */
@Composable
internal fun InputGalleryPage() {
    val spacing = OrbitTheme.spacing

    var short by remember { mutableStateOf("") }
    var filled by remember { mutableStateOf("Tower B, level 4") }
    var error by remember { mutableStateOf("not-an-email") }
    var query by remember { mutableStateOf("") }
    var typedQuery by remember { mutableStateOf("concrete pour") }

    // One per tier, so stepping one does not move the other two and the three heights can be compared
    // against a value that is not changing underneath them.
    var bags by remember { mutableStateOf(12) }
    var sheets by remember { mutableStateOf(1) }
    var rods by remember { mutableStateOf(999) }


    GallerySection("Text field · three sizes") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            OrbitFieldSize.entries.forEach { size ->
                OrbitTextField(
                    value = short,
                    onValueChange = { short = it },
                    label = "Location",
                    placeholder = "${size.name} · e.g. Tower B, level 4",
                    size = size,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    GallerySection("Quantity · three sizes") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            OrbitQuantityField(
                value = bags,
                onValueChange = { bags = it },
                label = "Bags of cement",
                size = OrbitFieldSize.Small,
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitQuantityField(
                value = sheets,
                onValueChange = { sheets = it },
                label = "Ply sheets",
                size = OrbitFieldSize.Medium,
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitQuantityField(
                value = rods,
                onValueChange = { rods = it },
                label = "Rebar rods",
                size = OrbitFieldSize.Large,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    // The two ends of the range and a disabled control, which is where the steppers have to show
    // their state: `sheets` starts at the floor and `rods` at the ceiling above, so one arm of each
    // of those is already greyed without anyone having to tap to see it.
    GallerySection("Quantity · disabled, and in error") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            OrbitQuantityField(
                value = 24,
                onValueChange = {},
                label = "Pallets",
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitQuantityField(
                value = 3,
                onValueChange = {},
                label = "Skips",
                state = OrbitFieldState.Error,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Text field · states") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            OrbitTextField(
                value = filled,
                onValueChange = { filled = it },
                label = "Location",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitTextField(
                value = error,
                onValueChange = { error = it },
                label = "Email",
                state = OrbitFieldState.Error,
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitTextField(
                value = "site.engineer@orbit.build",
                onValueChange = { },
                label = "Email",
                state = OrbitFieldState.Success,
                modifier = Modifier.fillMaxWidth(),
            )
            // Long enough to scroll, so the leading fade is visible. This is the case the fade
            // exists for: without it the row looks like a complete value that happens to be long.
            OrbitTextField(
                value = "drawings/2026/tower-b/level-4/structural-rev-c-final.dwg",
                onValueChange = { },
                label = "Linked file",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitTextField(
                value = "",
                onValueChange = {},
                label = "Approved by",
                placeholder = "Set automatically once the RFI is signed off",
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
            // A placeholder longer than the field, to check that it ellipsises rather than wrapping
            // and making an empty field taller than a filled one.
            OrbitTextField(
                value = "",
                onValueChange = {},
                label = "Notes",
                placeholder = "Describe what changed on site today, including any variation to the " +
                    "issued drawings and who authorised it",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Search · pill, three sizes") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            OrbitFieldSize.entries.forEach { size ->
                OrbitSearchField(
                    value = query,
                    onValueChange = { query = it },
                    size = size,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // With a query in it, so the clear button is visible.
            OrbitSearchField(
                value = typedQuery,
                onValueChange = { typedQuery = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Dropdown · stages from the work sequence") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            // The real list, ~100 stages. Open it on a device: the pinned search and add row, the
            // scrolling list beneath them and the truncation on the longer names are all things a
            // six-item demo would not show.
            ManagedStageDropdown(
                label = "Work stage",
                modifier = Modifier.fillMaxWidth(),
            )
            OrbitDropdownField(
                selected = null,
                options = emptyList(),
                onSelect = {},
                label = "Work stage, disabled",
                placeholder = "Not available on this project",
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    GallerySection("Dropdown · materials, multi select") {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.fieldGap)) {
            ManagedMaterialsDropdown(
                label = "Materials",
                initialSelection = listOf("Cement (OPC 53)", "Reinforcement Steel Fe500D"),
                modifier = Modifier.fillMaxWidth(),
            )
            // Empty, so the field sits at exactly the height of the text fields above it. Worth
            // seeing beside the filled one: the whole argument for chips is that the field grows,
            // and the check is that it does not start out grown.
            ManagedMaterialsDropdown(
                label = "Materials, empty",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
