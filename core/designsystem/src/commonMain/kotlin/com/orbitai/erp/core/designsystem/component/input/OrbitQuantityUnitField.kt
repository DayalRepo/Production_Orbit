package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitVerticalDivider
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * Quantity steppers and a unit picker in one field shell, split **50 / 50**: `− 12 + | Bags ▼`.
 *
 * The unit dropdown spans the **full field width** and includes search plus an optional add row.
 */
@Composable
fun OrbitQuantityUnitField(
    value: Int,
    onValueChange: (Int) -> Unit,
    selectedUnit: String?,
    units: List<String>,
    onUnitSelect: (String) -> Unit,
    quantityLabel: String,
    unitLabel: String,
    modifier: Modifier = Modifier,
    range: IntRange = DefaultQuantityRange,
    step: Int = 1,
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    unitPlaceholder: String = "Unit",
    unitSearchPlaceholder: String = "Search units",
    unitAddLabel: String = "Add unit",
    onAddUnitRequest: (() -> Unit)? = null,
    /** Override plus / minus / unit chevron size; defaults follow [size]. */
    iconSize: Dp? = null,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val density = LocalDensity.current

    val quantityInteraction = remember { MutableInteractionSource() }
    val unitInteraction = remember { MutableInteractionSource() }

    var unitExpanded by rememberDropdownExpanded()
    var unitQuery by remember { mutableStateOf("") }

    val minHeight = size.pick(sizing.fieldHeightSm, sizing.fieldHeightMd, sizing.fieldHeightLg)
    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)
    // Match [OrbitDropdownField] chevron sizes so material + unit menus read as one family.
    val resolvedIconSize = iconSize ?: size.pick(sizing.iconSm, sizing.iconSm, sizing.iconMd)
    val textStyle = size.pick(
        OrbitTheme.typography.bodyMedium,
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.typography.titleMedium,
    )

    var draft by remember(value) { mutableStateOf(value.toString()) }
    var slotWidth by remember { mutableIntStateOf(0) }
    var lineWidth by remember { mutableFloatStateOf(0f) }
    val quantityFocused by quantityInteraction.collectIsFocusedAsState()
    val overflowed = slotWidth > 0 && lineWidth > slotWidth

    var fieldAnchorWidth by remember { mutableStateOf(0.dp) }
    val visibleUnits = units.filterByQuery(unitQuery)
    val ink = if (enabled) content.textPrimary else content.textDisabled
    val hint = if (enabled) content.textSecondary else content.textSecondary.copy(OrbitAlpha.Disabled)

    val chevronRotation by animateFloatAsState(
        targetValue = if (unitExpanded) 180f else 0f,
        animationSpec = tween(OrbitDropdownOpenMs),
        label = "orbit-quantity-unit-chevron",
    )

    fun closeUnitMenu() {
        unitExpanded = false
        unitQuery = ""
    }

    Box(modifier = modifier.fillMaxWidth()) {
        OrbitFieldShell(
            interactionSource = quantityInteraction,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = minHeight,
            horizontalPadding = padding,
            enabled = enabled,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .onSizeChanged { fieldAnchorWidth = with(density) { it.width.toDp() } },
            contentGap = spacing.sm,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuantityStepperButton(
                    icon = OrbitIcons.MinusSign,
                    contentDescription = "Decrease $quantityLabel",
                    enabled = enabled && value > range.first,
                    iconSize = resolvedIconSize,
                    onClick = { onValueChange((value - step).coerceIn(range)) },
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onSizeChanged { slotWidth = it.width },
                    contentAlignment = Alignment.Center,
                ) {
                    if (enabled) {
                        CompositionLocalProvider(
                            LocalTextSelectionColors provides TextSelectionColors(
                                handleColor = control.actionContainer,
                                backgroundColor = control.actionContainer.copy(alpha = SelectionAlpha),
                            ),
                        ) {
                            OrbitFieldOverflowFade(overflowed = overflowed, atStart = quantityFocused) {
                                BasicTextField(
                                    value = draft,
                                    onValueChange = { typed ->
                                        val digits = typed.filter { it.isDigit() }
                                            .take(range.last.toString().length)
                                        draft = digits
                                        digits.toIntOrNull()?.let { parsed ->
                                            if (parsed in range) onValueChange(parsed)
                                        }
                                    },
                                    textStyle = textStyle.copy(
                                        color = ink,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    cursorBrush = SolidColor(control.actionContainer),
                                    interactionSource = quantityInteraction,
                                    onTextLayout = { result ->
                                        lineWidth = if (result.lineCount > 0) result.getLineRight(0) else 0f
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .orbitReleaseFocusWithKeyboard()
                                        .semantics { contentDescription = quantityLabel },
                                )
                            }
                        }
                    } else {
                        Text(
                            text = value.toString(),
                            style = textStyle,
                            fontWeight = FontWeight.Medium,
                            color = ink,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = quantityLabel },
                        )
                    }
                }

                QuantityStepperButton(
                    icon = OrbitIcons.PlusSign,
                    contentDescription = "Increase $quantityLabel",
                    enabled = enabled && value < range.last,
                    iconSize = resolvedIconSize,
                    onClick = { onValueChange((value + step).coerceIn(range)) },
                )
            }

            OrbitVerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = spacing.xs),
                color = control.controlBorder,
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        enabled = enabled,
                        interactionSource = unitInteraction,
                        indication = null,
                        role = Role.DropdownList,
                        onClick = { if (unitExpanded) closeUnitMenu() else unitExpanded = true },
                    )
                    .semantics {
                        contentDescription = "$unitLabel, ${selectedUnit ?: unitPlaceholder}"
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedUnit ?: unitPlaceholder,
                    style = textStyle,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedUnit != null) ink else hint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .clearAndSetSemantics {},
                )
                OrbitGlyph(
                    icon = OrbitIcons.ChevronDown,
                    size = resolvedIconSize,
                    tint = if (enabled) content.iconInactive else content.iconInactive.copy(OrbitAlpha.Disabled),
                    contentDescription = null,
                    minimumStroke = sizing.iconStrokeLight,
                    modifier = Modifier.rotate(chevronRotation),
                )
            }
        }

        OrbitDropdownMenu(
            expanded = unitExpanded,
            onDismiss = { closeUnitMenu() },
            width = fieldAnchorWidth,
            header = {
                OrbitDropdownHeader(
                    query = unitQuery,
                    onQueryChange = { unitQuery = it },
                    searchPlaceholder = unitSearchPlaceholder,
                    addLabel = if (onAddUnitRequest != null) unitAddLabel else null,
                    onAdd = onAddUnitRequest?.let {
                        {
                            closeUnitMenu()
                            it()
                        }
                    },
                )
            },
        ) {
            visibleUnits.forEach { unit ->
                OrbitDropdownRow(
                    label = unit,
                    selected = unit == selectedUnit,
                    onClick = {
                        onUnitSelect(unit)
                        closeUnitMenu()
                    },
                )
            }

            if (visibleUnits.isEmpty()) {
                QuantityUnitDropdownEmptyRow(query = unitQuery)
            }
        }
    }
}

@Composable
private fun QuantityUnitDropdownEmptyRow(query: String) {
    val spacing = OrbitTheme.spacing

    Text(
        text = if (query.isBlank()) "Nothing to choose from" else "No matches for \u201C$query\u201D",
        style = OrbitTheme.typography.bodyMedium,
        color = OrbitTheme.contentColors.textSecondary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md, vertical = spacing.sm),
    )
}

private val DefaultQuantityRange = 1..9_999_999
private const val SelectionAlpha = 0.24f
