package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonStyle
import com.orbitai.erp.core.designsystem.foundation.orbitGlassScrollbar
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The general-purpose text field: one line or many, in a glass container.
 *
 * ### Type size
 *
 * The input uses `bodyLarge`, a step above where a form label sits, and the placeholder uses the
 * identical style so nothing shifts at the moment the first character lands. That size is not a
 * preference: these forms get filled in on site, one-handed, on a bright screen, and a field that
 * renders what you typed smaller than the body copy around it is the field people mistype into. It
 * is an `sp`-based style like every other, so it still tracks the system font setting.
 *
 * ### Long text
 *
 * With [singleLine] false the field grows to [maxLines] and then scrolls rather than growing
 * further, which is what stops one pasted paragraph from pushing a Save button off a form. Once it
 * scrolls, two affordances appear:
 *
 * - a `Modifier.orbitGlassScrollbar` thumb, because without it a scrolled field looks exactly like
 *   an unscrolled one and the user gets no signal that their own text is hidden above the fold;
 * - an expand button, if [onExpand] is supplied — for the case scrolling cannot solve. Scrolling a
 *   five-line window is fine for writing and poor for reading back, so the field offers a way out to
 *   a full-height view rather than pretending a peephole is enough.
 *
 * Both are conditional on there actually being overflow, tested against the scroll range rather than
 * by counting lines, which cannot account for wrapping or for the current font scale. A field whose
 * content fits shows neither.
 *
 * ### Accessibility
 *
 * [label] is the accessible name and is required. [placeholder] is not a substitute: a placeholder
 * disappears on the first keystroke, so a field labelled only by its placeholder is unlabelled for a
 * screen-reader user and unlabelled for everyone else as soon as they start typing. Passing both is
 * the normal case — the label outside the field, the placeholder inside it.
 *
 * @param placeholder the greyed prompt shown while the field is empty.
 * @param supportingText helper or error text below the field. Say what to do, not just what is
 *   wrong: "Use dd/mm/yyyy" beats "Invalid date".
 * @param maxLines the growth cap for a multi-line field, in lines. Ignored when [singleLine].
 * @param onExpand opens a full-height view. Only offered once the text overflows.
 */
@Composable
fun OrbitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    error: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 5,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = if (singleLine) ImeAction.Done else ImeAction.Default,
    onExpand: (() -> Unit)? = null,
) {
    val control = OrbitTheme.controlColors
    val contentColors = OrbitTheme.contentColors
    val spacing = OrbitTheme.spacing

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    val textStyle = OrbitTheme.typography.bodyLarge
    val ink = if (enabled) contentColors.textPrimary else contentColors.textDisabled
    val hint = if (enabled) contentColors.textTertiary else contentColors.textDisabled

    val overflowing = !singleLine && scrollState.maxValue > 0

    // Derived from the style's own line height through the current density, so the cap follows both
    // the platform type scale and the user's font setting. A hardcoded dp value would clip the fifth
    // line the moment someone turned text size up.
    val lineCap = with(LocalDensity.current) { textStyle.lineHeight.toDp() } * maxLines

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        OrbitFieldShell(
            focused = focused,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = OrbitTheme.sizing.fieldHeight,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            error = error,
            // Top-aligned when multi-line, so the expand button stays level with the first line
            // rather than drifting down the field as it fills.
            verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
            verticalPadding = if (singleLine) spacing.sm else spacing.md,
            onRequestFocus = { focusRequester.requestFocus() },
        ) {
            Box(modifier = Modifier.weight(1f)) {
                // The scroll goes on a wrapper, never on the text field itself. Putting
                // `verticalScroll` directly on a `BasicTextField` hands it an unbounded height
                // constraint, and the field then measures out of the layout entirely — it renders,
                // but it takes no focus and never reaches the accessibility tree, so the box looks
                // like a working field and cannot be typed into by anyone, mouse or screen reader.
                Box(
                    modifier = if (singleLine) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            // The cap is here rather than on the shell so the container still grows
                            // with the font scale while the text is what actually scrolls.
                            .heightIn(max = lineCap)
                            // Before verticalScroll, so the thumb is measured against the viewport
                            // and stays put. After it, the thumb sizes to the content and scrolls
                            // away with the text.
                            .orbitGlassScrollbar(
                                scrollState = scrollState,
                                color = control.controlContent,
                                visible = overflowing,
                            )
                            .verticalScroll(scrollState)
                    },
                ) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        enabled = enabled,
                        textStyle = textStyle.copy(color = ink),
                        cursorBrush = SolidColor(control.controlContent),
                        singleLine = singleLine,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardType,
                            imeAction = imeAction,
                        ),
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            // Only inset once the thumb is there, so a short note is not
                            // permanently indented to reserve room for a bar it never gets.
                            .padding(end = if (overflowing) spacing.sm else 0.dp),
                    )
                }
                if (value.isEmpty() && placeholder != null) {
                    Text(text = placeholder, style = textStyle, color = hint)
                }
            }

            if (onExpand != null && overflowing) {
                OrbitIconButton(
                    contentDescription = "Expand $label to full view",
                    onClick = onExpand,
                    icon = OrbitIcons.Expand,
                    style = OrbitIconButtonStyle.Neutral,
                    size = OrbitIconButtonSize.Small,
                    state = if (enabled) OrbitButtonState.Active else OrbitButtonState.Disabled,
                    // Bottom, against the shell's Top alignment for everything else. At the top the
                    // button sits beside the first line and steals width from every line below it,
                    // which is the widest part of a paragraph; at the bottom it sits beside the last
                    // line, which is usually the short one, so the text keeps the full column and
                    // the button costs nothing.
                    modifier = Modifier.align(Alignment.Bottom),
                )
            }
        }

        if (supportingText != null) {
            Text(
                text = supportingText,
                style = OrbitTheme.typography.bodySmall,
                color = if (error) OrbitTheme.colorScheme.error else contentColors.textSecondary,
                modifier = Modifier.padding(horizontal = spacing.md),
            )
        }
    }
}
