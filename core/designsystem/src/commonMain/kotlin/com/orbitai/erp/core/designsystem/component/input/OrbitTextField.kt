package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A single-line text field.
 *
 * ### The placeholder is a hint, not a label
 *
 * [placeholder] disappears the moment anyone types, which makes it useless as the field's name — a
 * user who tabs away and back has no way to recall what the box was for, and neither does a screen
 * reader user who arrives at a filled field. So [label] exists separately and is required. It is
 * the accessible name whether or not a screen chooses to render it visibly.
 *
 * This is the mistake placeholder-only forms make, and it is worth being blunt about: a form built
 * entirely from placeholders is legible exactly once, before it is filled in.
 *
 * ### Typed text outweighs the hint it replaced
 *
 * The value is set a step heavier than the placeholder — [FontWeight.Medium] against the hint's
 * regular — and in the primary ink rather than the secondary. The two are the same size and sit on
 * the same baseline, so they occupy identical space and nothing shifts as the hint gives way.
 *
 * Weight rather than size for the difference, because size would move the baseline and make the
 * swap visible as a jump. What it buys is that a filled field and an empty one are distinguishable
 * from across the form at a glance, which is what you want when checking whether you have finished
 * one — and it stops the user's own input reading as placeholder text, which is the specific
 * confusion a same-weight value creates.
 *
 * ### Overflow
 *
 * The placeholder ellipsises rather than wrapping. A hint is expendable — the point of it is
 * recognisable in the first few words — and letting it wrap makes an empty field taller than the
 * same field once it has been typed into, so a form visibly shrinks as it is completed.
 *
 * The *value* does not ellipsise. It scrolls horizontally, and when it has scrolled the field draws
 * a short fade at the leading edge: the one thing worse than text running out of the box is text
 * running out of the box with no sign that it has. See [OrbitFieldOverflowFade].
 *
 * ### Sizing and scaling
 *
 * Three sizes, each a minimum height rather than a fixed one, so the field grows with the system
 * font scale instead of clipping (WCAG 1.4.4). All three clear the 48dp touch minimum — unlike
 * buttons, where Small goes under it deliberately, since a mistappable button can be tapped again
 * but an unhittable field cannot be typed into at all.
 *
 * @param label the accessible name. Required, and not rendered by this component — a screen that
 *   wants a visible label puts one above the field, where it stays after typing begins.
 * @param placeholder the in-field hint. Keep it an example ("e.g. Tower B, level 4") rather than a
 *   restatement of the label, which is wasted space.
 * @param state [OrbitFieldState.Error] or [OrbitFieldState.Success] to colour the rim. Independent
 *   of focus, because the usual moment for an error to exist is while the user stands in the field
 *   fixing it.
 */
@Composable
fun OrbitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val interactionSource = remember { MutableInteractionSource() }

    val minHeight = size.pick(sizing.fieldHeightSm, sizing.fieldHeightMd, sizing.fieldHeightLg)
    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)

    // Body sizes, not label sizes. What is typed here is content rather than chrome, and setting it
    // in a label style makes a filled form read as a list of captions.
    val base: TextStyle = size.pick(
        OrbitTheme.typography.bodyMedium,
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.extendedTypography.fieldLarge,
    )

    val ink = if (enabled) content.textPrimary else content.textPrimary.copy(OrbitAlpha.Disabled)
    val hint = if (enabled) content.textSecondary else content.textSecondary.copy(OrbitAlpha.Disabled)

    // Overflow has to be measured, not inferred from the value being non-empty — see the note on
    // OrbitFieldOverflowFade for what happens when it is guessed. `getLineRight` reports the line's
    // true width even past the constraint, which is what makes the comparison meaningful; the
    // reported `size` is already clamped and would always say it fits.
    var slotWidth by remember { mutableIntStateOf(0) }
    var lineWidth by remember { mutableFloatStateOf(0f) }
    val focused by interactionSource.collectIsFocusedAsState()
    val overflowed = singleLine && slotWidth > 0 && lineWidth > slotWidth

    OrbitFieldShell(
        interactionSource = interactionSource,
        shape = OrbitTheme.shapeTokens.field,
        minHeight = minHeight,
        horizontalPadding = padding,
        enabled = enabled,
        state = state,
        modifier = modifier,
    ) {
        leading?.invoke()

        Box(
            modifier = Modifier
                .weight(1f)
                .onSizeChanged { slotWidth = it.width },
            contentAlignment = Alignment.CenterStart,
        ) {
            // The selection handles and highlight default to Material's primary, which is not this
            // product's accent and shows up as a stray purple the first time anyone selects text.
            CompositionLocalProvider(
                LocalTextSelectionColors provides TextSelectionColors(
                    handleColor = control.actionContainer,
                    backgroundColor = control.actionContainer.copy(alpha = SelectionAlpha),
                ),
            ) {
                // While focused the caret is kept in view, so the text has scrolled left and the
                // hidden part is behind the caret; at rest the field shows the start of its value
                // and the hidden part is ahead.
                OrbitFieldOverflowFade(overflowed = overflowed, atStart = focused) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .orbitReleaseFocusWithKeyboard()
                            .semantics { contentDescription = label },
                        enabled = enabled,
                        readOnly = readOnly,
                        textStyle = base.copy(color = ink, fontWeight = FontWeight.Medium),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = singleLine,
                        visualTransformation = visualTransformation,
                        interactionSource = interactionSource,
                        cursorBrush = SolidColor(control.actionContainer),
                        onTextLayout = { result ->
                            lineWidth = if (result.lineCount > 0) result.getLineRight(0) else 0f
                        },
                    )
                }
            }

            if (value.isEmpty() && placeholder != null) {
                Text(
                    text = placeholder,
                    // Same size and baseline as the value, one weight lighter. Matching the metrics
                    // is what stops the hint's disappearance from nudging the layout.
                    style = base,
                    color = hint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    // The field already announces its label; a screen reader reading the hint on
                    // top of it says the same box twice.
                    modifier = Modifier.clearAndSetSemantics {},
                )
            }
        }

        trailing?.invoke()
    }
}

internal fun <T> OrbitFieldSize.pick(small: T, medium: T, large: T): T = when (this) {
    OrbitFieldSize.Small -> small
    OrbitFieldSize.Medium -> medium
    OrbitFieldSize.Large -> large
}

/** Selection highlight opacity — visible behind text without washing it out. */
private const val SelectionAlpha = 0.28f
