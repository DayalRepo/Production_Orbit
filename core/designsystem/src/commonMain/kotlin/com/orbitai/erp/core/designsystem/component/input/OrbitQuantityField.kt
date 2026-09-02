package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitCircularPressIndication
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A number with a stepper either side of it: `− [ 12 ] +`.
 *
 * ### Typing and stepping are both first-class
 *
 * The steppers are for adjusting and the field is for stating. Someone correcting a count of bags from
 * 12 to 13 taps once; someone entering 250 does not want to tap 250 times, and a stepper-only control
 * is the reason quantity entry in most apps is slower than a keyboard. So the middle is a real text
 * field with a numeric keyboard, and the buttons are a convenience on top of it rather than the only
 * way in.
 *
 * ### Why the text is held as a string
 *
 * The value the caller sees is an [Int], but what the user is editing is text, and those are not the
 * same thing while a field is being typed into. An empty field is not zero — it is a field somebody
 * has just cleared in order to type a different number — and coercing it to `0` on every keystroke
 * means the digit they type next lands after a zero they never entered. The draft string is kept
 * locally and [onValueChange] fires only when it parses inside [range], so the caller is never handed
 * a number the user did not mean, and never has to hold an invalid one.
 *
 * ### The bounds are enforced on the way in, not on submit
 *
 * [range] disables the stepper at each end and filters typed digits, rather than accepting anything
 * and clamping later. Clamping reads as the control fighting the user: type 1500 into a field that
 * stops at 999 and it becomes 999, which looks like a value they chose. Refusing the keystroke says
 * what happened at the moment it happened.
 *
 * @param value the current quantity. Held by the caller, so this control is stateless.
 * @param onValueChange fired with a number inside [range], never with a partial or empty edit.
 * @param label the accessible name — "Bags of cement". Required and not rendered, as with the other
 *   fields: a screen wanting a visible caption puts one above the control, where it survives typing.
 * @param range permitted values, inclusive at both ends.
 * @param step how far one stepper tap moves the value.
 * @param size the field tier. Sets height, horizontal padding, type size and the stepper buttons
 *   together, so `Small`, `Medium` and `Large` stay internally proportional.
 * @param minHeight override for the tier's height, for a caller fitting the control into a dense row.
 * @param horizontalPadding override for the tier's padding.
 * @param numberMinWidth floor for the number's own box, so a 7 and a 250 do not produce different
 *   control widths in the same column. Widen it for six-figure quantities.
 */
@Composable
fun OrbitQuantityField(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    range: IntRange = DefaultRange,
    step: Int = 1,
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    minHeight: Dp = size.pick(
        OrbitTheme.sizing.fieldHeightSm,
        OrbitTheme.sizing.fieldHeightMd,
        OrbitTheme.sizing.fieldHeightLg,
    ),
    horizontalPadding: Dp = size.pick(
        OrbitTheme.sizing.fieldPaddingSm,
        OrbitTheme.sizing.fieldPaddingMd,
        OrbitTheme.sizing.fieldPaddingLg,
    ),
    numberMinWidth: Dp = DefaultNumberMinWidth,
) {
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors

    val iconSize = size.pick(
        OrbitTheme.sizing.iconSm,
        OrbitTheme.sizing.iconSm,
        OrbitTheme.sizing.iconMd,
    )
    val textStyle = size.pick(
        OrbitTheme.typography.bodyMedium,
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.typography.titleMedium,
    )

    // The text being edited, reseeded whenever the caller's number changes for a reason other than a
    // keystroke here — a stepper tap, a form reset, a value arriving from the network.
    var draft by remember(value) { mutableStateOf(value.toString()) }
    val interactionSource = remember { MutableInteractionSource() }
    var slotWidth by remember { mutableIntStateOf(0) }
    var lineWidth by remember { mutableFloatStateOf(0f) }
    val focused by interactionSource.collectIsFocusedAsState()
    val overflowed = slotWidth > 0 && lineWidth > slotWidth

    val ink = if (enabled) content.textPrimary else content.textDisabled

    OrbitFieldShell(
        interactionSource = interactionSource,
        shape = OrbitTheme.shapeTokens.field,
        minHeight = minHeight,
        horizontalPadding = horizontalPadding,
        enabled = enabled,
        state = state,
        modifier = modifier,
    ) {
        QuantityStepperButton(
            icon = OrbitIcons.MinusSign,
            contentDescription = "Decrease $label",
            enabled = enabled && value > range.first,
            iconSize = iconSize,
            onClick = { onValueChange((value - step).coerceIn(range)) },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .widthIn(min = numberMinWidth)
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
                    OrbitFieldOverflowFade(overflowed = overflowed, atStart = focused) {
                        BasicTextField(
                            value = draft,
                            onValueChange = { typed ->
                                val maxDigits = range.last.toString().length
                                val digits = typed.filter { it.isDigit() }.take(maxDigits)
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
                            interactionSource = interactionSource,
                            onTextLayout = { result ->
                                lineWidth = if (result.lineCount > 0) result.getLineRight(0) else 0f
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .orbitReleaseFocusWithKeyboard()
                                .semantics { contentDescription = label },
                        )
                    }
                }
            } else {
                // A disabled stepper still has to show its number, and a text field that cannot be
                // focused is a worse way to do that than a label — which also keeps the disabled
                // control out of the focus order rather than in it and inert.
                Text(
                    text = value.toString(),
                    style = textStyle,
                    fontWeight = FontWeight.Medium,
                    color = ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = label },
                )
            }
        }

        QuantityStepperButton(
            icon = OrbitIcons.PlusSign,
            contentDescription = "Increase $label",
            enabled = enabled && value < range.last,
            iconSize = iconSize,
            onClick = { onValueChange((value + step).coerceIn(range)) },
        )
    }
}

@Composable
internal fun QuantityStepperButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    iconSize: Dp,
    onClick: () -> Unit,
) {
    val content = OrbitTheme.contentColors
    val sizing = OrbitTheme.sizing
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(sizing.minTouchTarget)
            .clip(CircleShape)
            .then(
                if (enabled) {
                    Modifier
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .indication(interactionSource, orbitCircularPressIndication())
                } else {
                    Modifier
                },
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        OrbitGlyph(
            icon = icon,
            size = iconSize,
            tint = if (enabled) content.iconPrimary else content.iconDisabled,
            contentDescription = null,
            minimumStroke = StepperIconStroke,
        )
    }
}

/** Heavier stroke so the bare +/- glyphs read clearly without a square container behind them. */
private val StepperIconStroke = 1.5.dp

/**
 * Whether [draft] is a quantity this field would accept.
 *
 * The whole of the control's validation, extracted so it can be tested without a device. Three cases
 * matter: a number in range, a number outside it, and a draft that is not a number at all — which
 * includes the empty string, the state the field is in for one keystroke every time somebody clears it
 * to type something else.
 */
internal fun isAcceptableQuantity(draft: String, range: IntRange): Boolean =
    draft.toIntOrNull()?.let { it in range } == true

/**
 * One to nine million nine hundred ninety-nine thousand nine hundred ninety-nine.
 *
 * Seven digits covers site-scale quantities without letting the field grow unbounded. Callers with
 * a different ceiling pass [range] explicitly.
 */
private val DefaultRange = 1..9_999_999

/**
 * Floor for the number's box: three digits at the largest tier, with room for the caret.
 *
 * A floor rather than a fixed width, because the box is weighted too — in a wide form the number takes
 * the room going spare and this value decides nothing. It binds in a narrow one, where without it a
 * quantity of 7 and a quantity of 250 rendered as visibly different controls in the same column.
 */
private val DefaultNumberMinWidth: Dp = 56.dp

/** Selection highlight behind dragged-over digits. Light enough to read the number through. */
private const val SelectionAlpha = 0.24f
