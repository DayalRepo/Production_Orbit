package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldShell
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.component.progress.OrbitProgressDefaults
import com.orbitai.erp.core.designsystem.component.progress.OrbitSegmentedProgress
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitPalette
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * One row on a checklist.
 *
 * @param id stable identity for toggle / remove callbacks.
 * @param label the task copy. Struck through when [checked].
 */
@Immutable
data class OrbitChecklistItem(
    val id: String,
    val label: String,
    val checked: Boolean = false,
)

/** `"4 of 6 complete"` — and `"0 of 0 complete"` when the list is empty. */
fun orbitChecklistProgressLabel(items: List<OrbitChecklistItem>): String {
    val total = items.size
    val done = items.count { it.checked }
    return "$done of $total complete"
}

fun orbitChecklistProgress(items: List<OrbitChecklistItem>): Float {
    if (items.isEmpty()) return 0f
    return items.count { it.checked }.toFloat() / items.size.toFloat()
}

/** Next unchecked item, or null when every item is done (or the list is empty). */
fun orbitChecklistNextOpen(items: List<OrbitChecklistItem>): OrbitChecklistItem? =
    items.firstOrNull { !it.checked }

fun orbitChecklistRemainingCount(items: List<OrbitChecklistItem>): Int =
    items.count { !it.checked }

/** `"2 left"` / `"All complete"` — collapsed-header copy that the progress bar does not say. */
fun orbitChecklistRemainingLabel(items: List<OrbitChecklistItem>): String {
    if (items.isEmpty()) return "No items"
    val left = orbitChecklistRemainingCount(items)
    return when (left) {
        0 -> "All complete"
        1 -> "1 left"
        else -> "$left left"
    }
}

/** Title and at least one item are required before a checklist can be created. */
fun orbitChecklistCanCreate(title: String, items: List<OrbitChecklistItem>): Boolean =
    title.trim().isNotEmpty() && items.isNotEmpty()

/**
 * Interactive checklist card.
 *
 * Header is always title + chevron and the progress row (blue pill track); the item list expands
 * underneath. Checked marks use theme blue. Glass + shadow; dark theme has no top mist.
 */
@Composable
fun OrbitChecklist(
    title: String,
    items: List<OrbitChecklistItem>,
    onCheckedChange: (id: String, checked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean? = null,
    onExpandedChange: ((Boolean) -> Unit)? = null,
    showProgress: Boolean = true,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.card
    val dark = OrbitTheme.isDark
    val progressInk = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50

    var internalExpanded by remember { mutableStateOf(false) }
    val isExpanded = expanded ?: internalExpanded
    val setExpanded: (Boolean) -> Unit = onExpandedChange ?: { internalExpanded = it }
    val headerInteraction = remember { MutableInteractionSource() }

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
            .semantics {
                contentDescription = buildString {
                    append(title)
                    append(". ")
                    append(orbitChecklistProgressLabel(items))
                    append(if (isExpanded) ", expanded" else ", collapsed")
                }
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = sizing.minTouchTarget)
                .orbitHandCursor()
                .clickable(
                    interactionSource = headerInteraction,
                    indication = null,
                    role = Role.Button,
                    onClick = { setExpanded(!isExpanded) },
                )
                .indication(headerInteraction, orbitPressIndication())
                .padding(start = spacing.lg, end = spacing.lg, top = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = OrbitTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = content.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            OrbitGlyph(
                icon = OrbitIcons.ChevronDown,
                size = sizing.iconSm,
                tint = content.iconPrimary,
                contentDescription = if (isExpanded) "Collapse checklist" else "Expand checklist",
                modifier = if (isExpanded) Modifier.rotate(180f) else Modifier,
            )
        }

        if (showProgress) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Text(
                    text = orbitChecklistProgressLabel(items),
                    style = OrbitTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = progressInk,
                    maxLines = 1,
                )
                if (items.isNotEmpty()) {
                    OrbitSegmentedProgress(
                        progress = orbitChecklistProgress(items),
                        segmentCount = OrbitProgressDefaults.ChecklistBarCount,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = sizing.progressTrackHeight),
                        contentDescription = orbitChecklistProgressLabel(items),
                    )
                }
                Text(
                    text = orbitChecklistRemainingLabel(items),
                    style = OrbitTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = content.textSecondary,
                    maxLines = 1,
                )
            }
        }

        if (!isExpanded && items.isNotEmpty()) {
            OrbitDivider(color = control.controlBorder)
            val next = orbitChecklistNextOpen(items)
            if (next != null) {
                ChecklistRow(
                    item = next,
                    onToggle = { onCheckedChange(next.id, !next.checked) },
                )
            } else {
                ChecklistAllCompleteRow()
            }
            Spacer(modifier = Modifier.size(spacing.sm))
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Column {
                OrbitDivider(color = control.controlBorder)
                items.forEach { item ->
                    ChecklistRow(
                        item = item,
                        onToggle = { onCheckedChange(item.id, !item.checked) },
                    )
                }
                Spacer(modifier = Modifier.size(spacing.sm))
            }
        }
    }
}

/**
 * Checklist creation surface.
 *
 * Required-field labels `Title *` / `List *`; plain `Enter` label. Title and add-item sit in
 * [OrbitFieldShell] containers; the plus glyph is inside the enter field. Cancel / Create decision
 * pair commits or abandons the draft.
 *
 * The blinking `|` after the keyboard dismisses is the **text caret** (cursor). Clearing focus when
 * the IME hides removes it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrbitChecklistEditor(
    title: String,
    onTitleChange: (String) -> Unit,
    items: List<OrbitChecklistItem>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (id: String) -> Unit,
    onCreate: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    titlePlaceholder: String = "e.g. Tower A pour readiness",
    itemPlaceholder: String = "e.g. Confirm pump booking",
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = OrbitTheme.shapeTokens.card
    val dark = OrbitTheme.isDark
    val canAdd = draft.isNotBlank()
    val canCreate = orbitChecklistCanCreate(title, items)
    val requiredStar = if (dark) OrbitPalette.Red70 else OrbitPalette.Red40

    val focusManager = LocalFocusManager.current
    val titleInteraction = remember { MutableInteractionSource() }
    val draftInteraction = remember { MutableInteractionSource() }
    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeVisible) {
        if (!imeVisible) focusManager.clearFocus()
    }

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
            .padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        RequiredFieldLabel(text = "Title", starColor = requiredStar)

        OrbitFieldShell(
            interactionSource = titleInteraction,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = sizing.fieldHeightMd,
            horizontalPadding = spacing.md,
            enabled = true,
            state = OrbitFieldState.Default,
            modifier = Modifier.fillMaxWidth(),
        ) {
            BasicTextField(
                value = title,
                onValueChange = onTitleChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                textStyle = OrbitTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = content.textPrimary,
                ),
                cursorBrush = SolidColor(control.actionContainer),
                singleLine = true,
                interactionSource = titleInteraction,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
                decorationBox = { inner ->
                    if (title.isEmpty()) {
                        Text(
                            text = titlePlaceholder,
                            style = OrbitTheme.typography.bodyLarge,
                            color = content.textTertiary,
                        )
                    }
                    inner()
                },
            )
        }

        RequiredFieldLabel(text = "List", starColor = requiredStar)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = sizing.fieldHeightMd)
                .semantics {
                    contentDescription = if (items.isEmpty()) {
                        "List, no items yet"
                    } else {
                        "List, ${items.size} items"
                    }
                },
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.md),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = "-",
                        style = OrbitTheme.typography.bodyMedium,
                        color = content.textTertiary,
                    )
                }
            } else {
                items.forEach { item ->
                    OrbitFieldShell(
                        interactionSource = remember { MutableInteractionSource() },
                        shape = OrbitTheme.shapeTokens.cardCompact,
                        minHeight = sizing.minTouchTarget,
                        horizontalPadding = spacing.md,
                        trailingPadding = spacing.xs,
                        enabled = true,
                        state = OrbitFieldState.Default,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OrbitGlyph(
                            icon = OrbitIcons.DashedLineCircle,
                            size = sizing.iconSm,
                            tint = content.iconInactive,
                            contentDescription = null,
                        )
                        Text(
                            text = item.label,
                            style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = content.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        OrbitIconButton(
                            contentDescription = "Remove item",
                            onClick = { onRemoveItem(item.id) },
                            icon = OrbitIcons.Cancel,
                            style = OrbitIconButtonStyle.Neutral,
                            size = OrbitIconButtonSize.Small,
                            ringed = false,
                        )
                    }
                }
            }
        }

        FieldLabel(text = "Enter")

        OrbitFieldShell(
            interactionSource = draftInteraction,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = sizing.fieldHeightMd,
            horizontalPadding = spacing.md,
            trailingPadding = spacing.xs,
            enabled = true,
            state = OrbitFieldState.Default,
            modifier = Modifier.fillMaxWidth(),
        ) {
            BasicTextField(
                value = draft,
                onValueChange = onDraftChange,
                modifier = Modifier.weight(1f),
                textStyle = OrbitTheme.typography.bodyMedium.copy(color = content.textPrimary),
                cursorBrush = SolidColor(control.actionContainer),
                singleLine = true,
                interactionSource = draftInteraction,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (canAdd) onAddItem()
                        focusManager.clearFocus()
                    },
                ),
                decorationBox = { inner ->
                    if (draft.isEmpty()) {
                        Text(
                            text = itemPlaceholder,
                            style = OrbitTheme.typography.bodyMedium,
                            color = content.textTertiary,
                        )
                    }
                    inner()
                },
            )
            Box(
                modifier = Modifier
                    .size(sizing.minTouchTarget)
                    .orbitHandCursor()
                    .clickable(
                        enabled = canAdd,
                        onClick = {
                            onAddItem()
                            focusManager.clearFocus()
                        },
                        role = Role.Button,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                OrbitGlyph(
                    icon = OrbitIcons.Add,
                    size = sizing.iconMd,
                    tint = if (canAdd) content.iconPrimary else content.iconInactive,
                    contentDescription = "Add item",
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OrbitButton(
                label = "Cancel",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                variant = OrbitButtonVariant.Destructive,
                size = OrbitButtonSize.Medium,
                icon = OrbitIcons.Cancel,
            )
            OrbitButton(
                label = "Create",
                onClick = { if (canCreate) onCreate() },
                modifier = Modifier.weight(1f),
                variant = OrbitButtonVariant.Primary,
                size = OrbitButtonSize.Medium,
                icon = OrbitIcons.Add,
                state = if (canCreate) OrbitButtonState.Active else OrbitButtonState.Disabled,
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    val content = OrbitTheme.contentColors
    Text(
        text = text,
        style = OrbitTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        color = content.textPrimary,
    )
}

@Composable
private fun RequiredFieldLabel(text: String, starColor: Color) {
    val content = OrbitTheme.contentColors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = OrbitTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = content.textPrimary,
        )
        Text(
            text = " *",
            style = OrbitTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = starColor,
        )
    }
}

@Composable
private fun ChecklistAllCompleteRow() {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val dark = OrbitTheme.isDark
    val active = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50
    val onActive = if (dark) OrbitPalette.Blue20 else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            .padding(horizontal = spacing.lg, vertical = spacing.sm)
            .semantics { contentDescription = "All items complete" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(sizing.iconMd)
                .clip(CircleShape)
                .background(active),
            contentAlignment = Alignment.Center,
        ) {
            OrbitGlyph(
                icon = OrbitIcons.Tick,
                size = sizing.iconSm,
                tint = onActive,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(spacing.md))
        Text(
            text = "All items complete",
            style = OrbitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = content.textSecondary,
        )
    }
}

@Composable
private fun ChecklistRow(
    item: OrbitChecklistItem,
    onToggle: () -> Unit,
) {
    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val dark = OrbitTheme.isDark
    val active = if (dark) OrbitPalette.Blue80 else OrbitPalette.Blue50
    val onActive = if (dark) OrbitPalette.Blue20 else Color.White
    val interaction = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            .orbitHandCursor()
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Checkbox,
                onClick = onToggle,
            )
            .indication(interaction, orbitPressIndication())
            .padding(horizontal = spacing.lg, vertical = spacing.sm)
            .semantics {
                contentDescription = buildString {
                    append(if (item.checked) "Completed, " else "Not completed, ")
                    append(item.label)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.checked) {
            Box(
                modifier = Modifier
                    .size(sizing.iconMd)
                    .clip(CircleShape)
                    .background(active),
                contentAlignment = Alignment.Center,
            ) {
                OrbitGlyph(
                    icon = OrbitIcons.Tick,
                    size = sizing.iconSm,
                    tint = onActive,
                    contentDescription = null,
                )
            }
        } else {
            OrbitGlyph(
                icon = OrbitIcons.DashedLineCircle,
                size = sizing.iconMd,
                tint = content.iconInactive,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(spacing.md))
        Text(
            text = item.label,
            style = OrbitTheme.typography.bodyMedium.copy(
                fontWeight = if (item.checked) FontWeight.Normal else FontWeight.Medium,
                textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None,
            ),
            color = if (item.checked) content.textTertiary else content.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}
