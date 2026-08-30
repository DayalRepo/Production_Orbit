package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * The message composer: attach on the left, dictate and send on the right.
 *
 * ### The shape reports the state
 *
 * Empty, the composer is a full pill. As soon as there is something in it, the corners tighten to a
 * rounded rectangle, and they relax back to a pill the moment it is cleared. The shape is doing real
 * work: a pill is the shape of a prompt — a thing you tap to begin — while a rectangle is the shape
 * of a container holding content, and the composer is genuinely both at different moments. It also
 * solves a practical problem, because a pill's corner radius is half its height, so once the field
 * grows to three or four lines the curve starts eating the text at the top and bottom lines. The
 * transition is animated, which keeps it legible as a change of state rather than a glitch.
 *
 * ### Buttons
 *
 * Attachment sits on the left; microphone and send sit on the right, in that order, because send is
 * the action the thumb should find at the outer edge. All three are present at all times, so the row
 * never reflows under the user's hand as they type — a control that appears mid-sentence moves every
 * other control along with it, which is how people tap the wrong one.
 *
 * Send is the one filled control, because it is the one that commits, and it goes inactive rather
 * than disabled while the field is empty: there is nothing to send, but the button is still the
 * answer to "how do I send this" and a fully greyed control stops answering that.
 *
 * The side glyphs are a size up from the inline controls elsewhere. They are the only affordances in
 * a bar the user operates one-handed, often without looking directly at them.
 *
 * ### Growth
 *
 * The field grows to [maxLines] and then scrolls, with the same glass thumb the multi-line
 * [OrbitTextField] uses. A composer that grows without limit eats the conversation it belongs to,
 * which is the thing the user is writing about.
 *
 * @param onAttach null hides the attachment button — for a channel where files are not allowed.
 * @param onDictate null hides the microphone, e.g. where speech permission was declined.
 */
@Composable
fun OrbitMessageField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Send Message",
    label: String = "Message",
    enabled: Boolean = true,
    maxLines: Int = 4,
    onAttach: (() -> Unit)? = null,
    onDictate: (() -> Unit)? = null,
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

    val hasText = value.isNotEmpty()
    val overflowing = scrollState.maxValue > 0
    val lineCap = with(LocalDensity.current) { textStyle.lineHeight.toDp() } * maxLines

    // Animated as a radius rather than by swapping two shapes, so the corner travels instead of
    // snapping. The empty radius is deliberately larger than the composer can ever be tall, which is
    // what makes `RoundedCornerShape` clamp it to a true pill.
    val corner by animateDpAsState(
        targetValue = if (hasText) ComposerTypingCorner else PillCorner,
        animationSpec = tween(ShapeMs),
        label = "orbit-composer-corner",
    )

    OrbitFieldShell(
        focused = focused,
        shape = RoundedCornerShape(corner),
        minHeight = OrbitTheme.sizing.minTouchTarget,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        // Bottom-aligned, so the buttons stay beside the line being typed as the field grows
        // upward. Top alignment would leave send stranded next to the first line.
        verticalAlignment = Alignment.Bottom,
        horizontalPadding = spacing.sm,
        verticalPadding = spacing.xs,
        onRequestFocus = { focusRequester.requestFocus() },
    ) {
        if (onAttach != null) {
            OrbitIconButton(
                contentDescription = "Attach a file to this $label",
                onClick = onAttach,
                icon = OrbitIcons.Attachment,
                style = OrbitIconButtonStyle.Neutral,
                size = OrbitIconButtonSize.Medium,
                state = if (enabled) OrbitButtonState.Active else OrbitButtonState.Disabled,
            )
        }

        Box(modifier = Modifier.weight(1f).padding(vertical = spacing.sm)) {
            // The scroll goes on a wrapper, never on the text field itself — `verticalScroll` on a
            // `BasicTextField` gives it an unbounded height constraint and the field measures out of
            // the layout: it still draws, but it takes no focus and never reaches the accessibility
            // tree. The composer looks fine and cannot be typed into.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = lineCap)
                    // Before verticalScroll, so the thumb is measured against the viewport and stays
                    // put. After it, the thumb sizes to the content and scrolls away with the text.
                    .orbitGlassScrollbar(
                        scrollState = scrollState,
                        color = control.controlContent,
                        visible = overflowing,
                    )
                    .verticalScroll(scrollState),
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    textStyle = textStyle.copy(color = ink),
                    cursorBrush = SolidColor(control.controlContent),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        // Not ImeAction.Send: the return key has to be able to insert a newline,
                        // because a site update is frequently a short list rather than one
                        // sentence. Send is the button.
                        imeAction = ImeAction.Default,
                    ),
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .padding(end = if (overflowing) spacing.sm else 0.dp),
                )
            }
            if (!hasText) {
                Text(text = placeholder, style = textStyle, color = hint)
            }
        }

        if (onDictate != null) {
            OrbitIconButton(
                contentDescription = "Dictate a $label",
                onClick = onDictate,
                icon = OrbitIcons.Mic,
                style = OrbitIconButtonStyle.Neutral,
                size = OrbitIconButtonSize.Medium,
                state = if (enabled) OrbitButtonState.Active else OrbitButtonState.Disabled,
            )
        }

        OrbitIconButton(
            contentDescription = "Send $label",
            onClick = onSend,
            icon = OrbitIcons.Sent,
            style = OrbitIconButtonStyle.Accent,
            size = OrbitIconButtonSize.Medium,
            state = when {
                !enabled -> OrbitButtonState.Disabled
                hasText -> OrbitButtonState.Active
                else -> OrbitButtonState.Inactive
            },
        )
    }
}

/**
 * Larger than any composer height, so `RoundedCornerShape` clamps it into a true pill.
 *
 * A literal rather than `RoundedCornerShape(percent = 50)`, because a percentage cannot be animated
 * against a dp — and animating the corner is the whole point.
 */
internal val PillCorner = 999.dp

/** Tight enough to read as a container, loose enough not to look like a plain box. */
internal val ComposerTypingCorner = 20.dp

private const val ShapeMs = 200
