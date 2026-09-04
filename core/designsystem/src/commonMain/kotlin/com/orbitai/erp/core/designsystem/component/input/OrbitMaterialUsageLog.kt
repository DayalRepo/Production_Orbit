package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One line on a materials-used log: what was consumed, how much, and in which unit.
 *
 * Quantity is an integer because site counts are bags, sheets and rods — not fractional tonnes typed
 * into a free field. The unit lives beside the number so a line cannot be saved as "42" with no
 * measure.
 */
@Immutable
data class OrbitMaterialUsageLine(
    val id: String,
    val material: String? = null,
    val quantity: Int = 1,
    val unit: String? = null,
)

/** A line is complete only when material, quantity and unit are all present. */
fun orbitMaterialUsageLineComplete(line: OrbitMaterialUsageLine): Boolean =
    !line.material.isNullOrBlank() && line.quantity > 0 && !line.unit.isNullOrBlank()

fun orbitMaterialUsageCompleteCount(lines: List<OrbitMaterialUsageLine>): Int =
    lines.count(::orbitMaterialUsageLineComplete)

/**
 * Suggested stock unit for a material name — used to auto-fill the unit when a material is picked,
 * while still leaving the unit dropdown free for a manual override.
 */
fun orbitSuggestedUnitForMaterial(material: String): String? {
    val name = material.lowercase()
    return when {
        // "cement" is a substring of "reinforcement" — check steel/rebar first.
        "reinforcement" in name || "rebar" in name || "binding wire" in name -> "Kg"
        "structural steel" in name -> "Tonnes"
        "cement" in name -> "Bags"
        "concrete" in name || "ready-mix" in name -> "Cubic Metres"
        "aggregate" in name || name.endsWith(" sand") || " m-sand" in name || name == "m-sand" ||
            "river sand" in name -> "Cubic Metres"
        "block" in name -> "Nos"
        "membrane" in name || "sheet" in name || "ply" in name -> "Sheets"
        "tile" in name -> "Square Metres"
        "pipe" in name || "conduit" in name -> "Metres"
        "putty" in name || "primer" in name || "paint" in name -> "Kg"
        else -> null
    }
}

/**
 * Site log for materials consumed on a task.
 *
 * Each row is material → quantity + unit, with a quiet text remove instead of an icon button so the
 * quantity field stays the visual centre. Add is a full-width primary pill with the usual glass and
 * shadow. Picking a material suggests a unit via [suggestedUnitForMaterial]; the unit dropdown still
 * accepts a manual choice.
 */
@Composable
fun OrbitMaterialUsageLog(
    lines: List<OrbitMaterialUsageLine>,
    materials: List<String>,
    units: List<String>,
    onMaterialSelect: (id: String, material: String) -> Unit,
    onQuantityChange: (id: String, quantity: Int) -> Unit,
    onUnitSelect: (id: String, unit: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Materials used",
    addLabel: String = "Add material",
    suggestedUnitForMaterial: (String) -> String? = ::orbitSuggestedUnitForMaterial,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.card
    val dark = OrbitTheme.isDark
    val complete = orbitMaterialUsageCompleteCount(lines)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowButton)
            .clip(shape)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (dark) 0f else OrbitGlass.SurfaceHighlightLight,
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
                sheen = if (dark) 1f else OrbitGlass.Sheen,
            )
            .padding(spacing.lg)
            .semantics {
                contentDescription = "$title, $complete of ${lines.size} logs complete"
            },
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
            Text(
                text = title,
                style = OrbitTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = content.textPrimary,
            )
            Text(
                text = if (lines.isEmpty()) {
                    "No materials logged"
                } else {
                    "$complete of ${lines.size} logged"
                },
                style = OrbitTheme.typography.bodySmall,
                color = content.textSecondary,
            )
        }

        OrbitDivider(color = control.controlBorder)

        lines.forEachIndexed { index, line ->
            if (index > 0) {
                OrbitDivider(color = control.controlBorder)
            }
            MaterialUsageRow(
                line = line,
                index = index + 1,
                materials = materials,
                units = units,
                canRemove = lines.size > 1 || line.material != null || line.unit != null,
                onMaterialSelect = { material ->
                    onMaterialSelect(line.id, material)
                    suggestedUnitForMaterial(material)?.let { onUnitSelect(line.id, it) }
                },
                onQuantityChange = { onQuantityChange(line.id, it) },
                onUnitSelect = { onUnitSelect(line.id, it) },
                onRemove = { onRemove(line.id) },
            )
        }

        OrbitButton(
            label = addLabel,
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth(),
            variant = OrbitButtonVariant.Primary,
            size = OrbitButtonSize.Medium,
            icon = OrbitIcons.Add,
        )
    }
}

@Composable
private fun MaterialUsageRow(
    line: OrbitMaterialUsageLine,
    index: Int,
    materials: List<String>,
    units: List<String>,
    canRemove: Boolean,
    onMaterialSelect: (String) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onUnitSelect: (String) -> Unit,
    onRemove: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val danger = OrbitBadgeTone.Red.colors.label
    val materialName = line.material ?: "material"
    val removeInteraction = remember { MutableInteractionSource() }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Log $index",
                style = OrbitTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = content.textSecondary,
            )
            if (canRemove) {
                Text(
                    text = "Remove",
                    style = OrbitTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = danger,
                    modifier = Modifier
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = removeInteraction,
                            indication = null,
                            role = Role.Button,
                            onClick = onRemove,
                        )
                        .indication(removeInteraction, orbitPressIndication())
                        .padding(vertical = spacing.xxs)
                        .semantics { contentDescription = "Remove $materialName" },
                )
            }
        }
        OrbitDropdownField(
            selected = line.material,
            options = materials,
            onSelect = onMaterialSelect,
            label = "Material",
            placeholder = "Select material",
            searchPlaceholder = "Search materials",
            modifier = Modifier.fillMaxWidth(),
            size = OrbitFieldSize.Medium,
        )
        OrbitQuantityUnitField(
            value = line.quantity,
            onValueChange = onQuantityChange,
            selectedUnit = line.unit,
            units = units,
            onUnitSelect = onUnitSelect,
            quantityLabel = "Quantity of $materialName",
            unitLabel = "Unit for $materialName",
            unitPlaceholder = "Unit",
            size = OrbitFieldSize.Medium,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
