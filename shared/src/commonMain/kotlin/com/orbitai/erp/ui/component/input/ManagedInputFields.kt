package com.orbitai.erp.ui.component.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.orbitai.erp.core.designsystem.component.input.OrbitDescriptionField
import com.orbitai.erp.core.designsystem.component.input.OrbitDescriptionTranslatePanel
import com.orbitai.erp.core.designsystem.component.input.OrbitQuantityUnitField
import com.orbitai.erp.ui.component.dropdown.ConstructionUnits

@Composable
fun ManagedQuantityUnitField(
    modifier: Modifier = Modifier,
    quantityLabel: String = "Quantity",
    unitLabel: String = "Unit",
) {
    var quantity by remember { mutableStateOf(12) }
    var unit by remember { mutableStateOf<String?>("Bags") }
    var units by remember { mutableStateOf(ConstructionUnits) }

    OrbitQuantityUnitField(
        value = quantity,
        onValueChange = { quantity = it },
        selectedUnit = unit,
        units = units,
        onUnitSelect = { unit = it },
        quantityLabel = quantityLabel,
        unitLabel = unitLabel,
        modifier = modifier,
        onAddUnitRequest = {
            val newUnit = "Custom unit ${units.size + 1}"
            units = units + newUnit
            unit = newUnit
        },
    )
}

@Composable
fun ManagedDescriptionField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
) {
    var aiMenuOpen by remember { mutableStateOf(false) }
    var translateOpen by remember { mutableStateOf(false) }
    var descriptionExpanded by remember { mutableStateOf(false) }

    OrbitDescriptionField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        expanded = descriptionExpanded,
        onExpandedChange = { descriptionExpanded = it },
        aiAssistEnabled = true,
        aiMenuExpanded = aiMenuOpen,
        onAiMenuExpandedChange = {
            aiMenuOpen = it
            if (it) translateOpen = false
        },
        onAiRewrite = {
            aiMenuOpen = false
            if (value.isNotBlank()) {
                onValueChange(
                    "$value\n\n" +
                        "[Orbit AI] Expanded into a fuller handover-style description with " +
                        "scope, materials, and completion notes.",
                )
            }
        },
        onAiTranslate = {
            aiMenuOpen = false
            translateOpen = true
        },
        aiPanel = { width ->
            OrbitDescriptionTranslatePanel(
                expanded = translateOpen,
                onLanguageSelect = { language ->
                    if (value.isNotBlank()) {
                        onValueChange("[$language] $value")
                    }
                    translateOpen = false
                },
                onDismiss = { translateOpen = false },
                width = width,
            )
        },
    )
}
