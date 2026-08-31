package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The search field: a pill with a magnifier in it.
 *
 * ### Why this is not just `OrbitTextField` with an icon
 *
 * It very nearly is, and the two share [OrbitFieldShell] so the glass, rim and focus behaviour
 * cannot drift apart. What differs is worth a separate component:
 *
 * - **The shape is a pill**, where a text field is a 10dp rounded rectangle. That is the one piece
 *   of shape vocabulary this product spends on meaning: rounded rectangles are things you fill in,
 *   pills are things you act with. A search box is closer to a control than to a form field — you
 *   type into it to *do* something immediately, not to record a value — and every platform has
 *   converged on the pill for exactly that reason.
 * - **It clears itself.** A trailing ✕ appears once there is a query, because backspacing out of a
 *   search term is a chore and abandoning a search is the most common thing anyone does with one.
 * - **The IME action is Search**, so the on-screen keyboard offers a search key rather than a
 *   newline.
 *
 * The magnifier is decorative and explicitly undescribed. It restates the field's own name, and
 * "magnifier icon, Search, edit box" is three ways of saying one thing.
 */
@Composable
fun OrbitSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    label: String = "Search",
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    enabled: Boolean = true,
    onSearch: (() -> Unit)? = null,
) {
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }

    val minHeight = size.pick(sizing.fieldHeightSm, sizing.fieldHeightMd, sizing.fieldHeightLg)
    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)
    val glyph = size.pick(sizing.iconSm, sizing.iconMd, sizing.iconMd)

    val base: TextStyle = size.pick(
        // A Small search field takes the same type as a Medium one. The size step exists to make the
        // *box* shorter where it is a tool inside a panel rather than a field on a form; shrinking
        // the type along with it made the query harder to read than the list it was filtering, which
        // is backwards — the query is the thing being edited.
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.extendedTypography.fieldLarge,
    )

    var slotWidth by remember { mutableIntStateOf(0) }
    var lineWidth by remember { mutableFloatStateOf(0f) }
    val focused by interactionSource.collectIsFocusedAsState()
    val overflowed = slotWidth > 0 && lineWidth > slotWidth

    val ink = if (enabled) content.textPrimary else content.textPrimary.copy(OrbitAlpha.Disabled)
    val hint = if (enabled) content.textSecondary else content.textSecondary.copy(OrbitAlpha.Disabled)

    OrbitFieldShell(
        interactionSource = interactionSource,
        // The one shape difference from a text field, and the reason this is its own component.
        shape = OrbitTheme.shapeTokens.chip,
        minHeight = minHeight,
        horizontalPadding = padding,
        // Tighter than the default. The magnifier and the placeholder are one label, not two slots.
        contentGap = OrbitTheme.spacing.xs,
        enabled = enabled,
        // A search box has no valid or invalid value — every string is a legitimate query, including
        // one that matches nothing. There is nothing for a state rim to report.
        state = OrbitFieldState.Default,
        modifier = modifier,
    ) {
        OrbitGlyph(
            icon = OrbitIcons.Search,
            size = glyph,
            tint = hint,
            contentDescription = null,
            // Beside type, so the full stroke floor. The lighter weights are for glyphs standing
            // alone; this one has a line of body text next to it to hold its own against.
            minimumStroke = sizing.iconStrokeWidth,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .onSizeChanged { slotWidth = it.width },
            contentAlignment = Alignment.CenterStart,
        ) {
            CompositionLocalProvider(
                LocalTextSelectionColors provides TextSelectionColors(
                    handleColor = control.actionContainer,
                    backgroundColor = control.actionContainer.copy(alpha = SelectionAlpha),
                ),
            ) {
                OrbitFieldOverflowFade(overflowed = overflowed, atStart = focused) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .orbitReleaseFocusWithKeyboard()
                        .semantics { contentDescription = label },
                    enabled = enabled,
                    // A step heavier than the placeholder, so a live query does not read as the
                    // hint it replaced.
                    textStyle = base.copy(color = ink, fontWeight = FontWeight.Medium),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke() }),
                    singleLine = true,
                    interactionSource = interactionSource,
                    cursorBrush = SolidColor(control.actionContainer),
                    onTextLayout = { result ->
                        lineWidth = if (result.lineCount > 0) result.getLineRight(0) else 0f
                    },
                )
                }
            }

            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = base,
                    color = hint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }
        }

        if (value.isNotEmpty() && enabled) {
            Box(
                modifier = Modifier
                    // The full touch minimum even though the glyph is small. A clear button that is
                    // hard to hit is worse than none: the miss lands in the field, which focuses it
                    // and opens the keyboard, so a failed clear actively makes things worse.
                    .size(sizing.minTouchTarget)
                    .orbitHandCursor()
                    .clickable(
                        role = Role.Button,
                        onClick = { onValueChange("") },
                    )
                    .semantics { contentDescription = "Clear search" },
                contentAlignment = Alignment.Center,
            ) {
                OrbitGlyph(
                    icon = OrbitIcons.Cancel,
                    size = glyph,
                    tint = hint,
                    contentDescription = null,
                    minimumStroke = sizing.iconStrokeLight,
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }
        }
    }
}

private const val SelectionAlpha = 0.28f
