package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitCircularPressIndication
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A dropdown that collects several answers and keeps them in the field as removable chips.
 *
 * ```
 *  ---------------------------------------
 * | [Cement x] [Rebar x] [Sand x]      v  |
 *  ---------------------------------------
 * | [ Search materials                 ]  |
 * | [+] Add material                      |
 * |=======================================|
 * |  Cement                            v  |
 * |  ------------------                   |
 * |  Aggregate                            |
 *  ---------------------------------------
 * ```
 *
 * Built for materials on a requisition, where the answer is genuinely a set — a pour needs cement
 * and sand and aggregate, and the order they were picked in means nothing.
 *
 * ### Chips in the field, rather than a count
 *
 * "3 selected" fits on one line and tells the user almost nothing: to find out whether they
 * remembered the rebar they have to reopen the list and read down it. Chips cost height, and the
 * height is the point — the field wraps to as many rows as the selection needs, so the answer is
 * legible without reopening anything. On a form that is about to become a purchase order, "what did
 * I actually ask for" is worth several lines of screen.
 *
 * Each chip carries its own remove control. Removing by reopening the list and untapping the row is
 * two navigations to undo one tap, and it asks the user to find an item in a list of forty when they
 * are already looking at the thing they want gone.
 *
 * ### Taken options are disabled, not hidden
 *
 * A chosen material stays in the list, dimmed, ticked and inert. Hiding it would make the list
 * shorter every time it is used, so rows slide up under a finger mid-scroll and the user loses their
 * place — and a list that no longer contains cement invites the reasonable conclusion that cement
 * was never there. Dimming answers "have I got this one" in place, which is the actual question.
 *
 * ### The panel stays open
 *
 * Unlike [OrbitDropdownField], picking does not close the list. The user is assembling a set and
 * almost always has another one to add; closing after each tap turns a five-material requisition
 * into five open-scroll-tap cycles. It closes on a tap outside, on the chevron, or on back.
 *
 * @param selected in the caller's order, drawn left to right and wrapped. A list rather than a set
 *   so the chips do not reshuffle between recompositions, which would move a remove button out from
 *   under a finger already reaching for it.
 * @param onAddRequest null to leave the "add" row out. See [OrbitDropdownField] for why creating the
 *   entry belongs to the caller.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrbitMultiSelectField(
    selected: List<String>,
    options: List<String>,
    onToggle: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    searchPlaceholder: String = "Search",
    addLabel: String? = null,
    onAddRequest: (() -> Unit)? = null,
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }

    var expanded by rememberDropdownExpanded()
    var query by remember { mutableStateOf("") }

    val density = LocalDensity.current
    var anchorWidth by remember { mutableStateOf(0.dp) }

    val minHeight = size.pick(sizing.fieldHeightSm, sizing.fieldHeightMd, sizing.fieldHeightLg)
    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(OrbitDropdownOpenMs),
        label = "orbit-multiselect-chevron",
    )

    val visible = options.filterByQuery(query)

    fun close() {
        expanded = false
        query = ""
    }

    Box(modifier = modifier) {
        OrbitFieldShell(
            interactionSource = interactionSource,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = minHeight,
            horizontalPadding = padding,
            enabled = enabled,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidth = with(density) { it.width.toDp() } }
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.DropdownList,
                ) { if (expanded) close() else expanded = true }
                // Spoken as one summary, because the chips are individually focusable controls and a
                // screen reader landing on the field itself should hear what is in it before being
                // walked through the remove buttons one at a time.
                .semantics {
                    contentDescription = if (selected.isEmpty()) {
                        "$label, $placeholder"
                    } else {
                        "$label, ${selected.size} selected: ${selected.joinToString(", ")}"
                    }
                },
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (selected.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = size.pick(
                            OrbitTheme.typography.bodyMedium,
                            OrbitTheme.typography.bodyLarge,
                            OrbitTheme.extendedTypography.fieldLarge,
                        ),
                        fontWeight = FontWeight.Medium,
                        color = if (enabled) {
                            content.textSecondary
                        } else {
                            content.textSecondary.copy(OrbitAlpha.Disabled)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clearAndSetSemantics {},
                    )
                } else {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Air above and below only when there are chips. An empty field keeps
                            // the exact height of the text fields beside it; a filled one grows, and
                            // the padding stops the first and last rows from touching the rim.
                            .padding(vertical = spacing.xxs),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xxs),
                        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                    ) {
                        selected.forEach { item ->
                            // Keyed on the material, which is the fix for a genuinely confusing bug:
                            // removing a chip made the *next* chip flash as though it had been
                            // pressed. Without a key, Compose reuses the slot — the chip that shifts
                            // up into the removed one's position inherits its composition, and with it
                            // the press animation that was still running on the control the finger had
                            // just lifted from. So the user tapped one X and watched a different chip
                            // light up.
                            key(item) {
                                SelectedChip(
                                    label = item,
                                    enabled = enabled,
                                    onRemove = { onToggle(item) },
                                )
                            }
                        }
                    }
                }
            }

            OrbitGlyph(
                icon = OrbitIcons.ChevronDown,
                size = sizing.iconMd,
                tint = if (enabled) {
                    content.iconInactive
                } else {
                    content.iconInactive.copy(OrbitAlpha.Disabled)
                },
                contentDescription = null,
                minimumStroke = sizing.iconStrokeLight,
                modifier = Modifier.rotate(rotation),
            )
        }

        OrbitDropdownMenu(
            expanded = expanded,
            onDismiss = { close() },
            width = anchorWidth,
            header = {
                OrbitDropdownHeader(
                    query = query,
                    onQueryChange = { query = it },
                    searchPlaceholder = searchPlaceholder,
                    addLabel = addLabel,
                    onAdd = onAddRequest?.let {
                        {
                            close()
                            it()
                        }
                    },
                )
            },
        ) {
            // Chosen first, then the rest, each keeping the caller's order within its group.
            //
            // Two things were wrong with leaving them in place and greying them out. A user checking
            // what they had picked had to read the whole list to find the ticks, which is the job the
            // chips in the field do badly enough already at four selections. And a disabled row is a
            // dead end: the only way to undo a mis-tap was to close the panel and find the chip's X,
            // so the list could add but never subtract. Now the ticked rows sit together at the top
            // and tapping one takes it off — the same gesture that put it on, which is what makes the
            // row read as a toggle rather than as a button that has been used up.
            val ordered = remember(visible, selected) {
                visible.sortedByDescending { it in selected }
            }

            ordered.forEach { option ->
                OrbitDropdownRow(
                    label = option,
                    selected = option in selected,
                    onClick = { onToggle(option) },
                )
            }

            if (visible.isEmpty()) {
                Text(
                    text = if (query.isBlank()) {
                        "Nothing to choose from"
                    } else {
                        "No matches for \u201C$query\u201D"
                    },
                    style = OrbitTheme.typography.bodyMedium,
                    color = content.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.md, vertical = spacing.sm),
                )
            }
        }
    }
}

/**
 * One chosen value sitting in the field, with the control that takes it back out.
 *
 * ### Cornered, not a pill
 *
 * Every other small rounded thing in this system — filter chips, badges, buttons — is a full pill,
 * and that is precisely the problem: a row of pills inside a text field reads as a row of buttons
 * the user is expected to press. These are entered data. Squaring the corners to a small radius
 * separates "value I have supplied" from "control I can operate", while staying soft enough to sit
 * inside a 10dp field without the two radii fighting each other.
 *
 * It is also deliberately not
 * [OrbitChip][com.orbitai.erp.core.designsystem.component.status.OrbitChip]. Borrowing that
 * component's selected inversion would put high-contrast pressed-looking controls inside a field,
 * and the field would read as a toolbar.
 *
 * The X is inside the chip rather than beside it, because a remove control that is not visibly part
 * of the thing it removes is ambiguous the moment there are three chips in a row.
 */
@Composable
private fun SelectedChip(
    label: String,
    enabled: Boolean,
    onRemove: () -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors

    Box(
        modifier = Modifier
            .heightIn(min = ChipMinHeight)
            .background(control.controlContainer, OrbitTheme.shapeTokens.inputChip)
            .border(
                width = sizing.hairline,
                color = control.controlBorder,
                shape = OrbitTheme.shapeTokens.inputChip,
            )
            .padding(start = spacing.sm, end = spacing.xxs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = label,
                style = OrbitTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) content.textPrimary else content.textDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clearAndSetSemantics {},
            )

            // Its own source, keyed to the label, and its own indication clipped to a circle. Belt
            // and braces alongside the `key(item)` above: a source that outlives the control it
            // belonged to is what lets a press animation finish somewhere it was never started, and
            // the ripple is bounded to a circle so it reads as a press on the X rather than a wash
            // across the chip.
            val removeInteraction = remember(label) { MutableInteractionSource() }

            // No surface of its own — the glyph sits directly on the chip.
            //
            // A raised tile was tried here and read as a control stuck onto a control: a chip is
            // already a small object inside a field, and giving the X its own fill, rim and shadow
            // made the chip look like two nested buttons and drew far more attention to deleting a
            // material than to the material itself. The chip's rim is enough of a boundary for the
            // glyph inside it; the circle-bounded ripple below is what confirms the press.
            Box(
                modifier = Modifier
                    .size(sizing.iconMd)
                    .clip(CircleShape)
                    .indication(removeInteraction, orbitCircularPressIndication())
                    .clickable(
                        interactionSource = removeInteraction,
                        indication = null,
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onRemove,
                    )
                    // The field speaks the full selection, so this control is the one thing inside
                    // the chip a screen reader needs to reach — and it names what it will remove
                    // rather than saying a bare "Remove".
                    .semantics { contentDescription = "Remove $label" },
                contentAlignment = Alignment.Center,
            ) {
                OrbitGlyph(
                    icon = OrbitIcons.Cancel,
                    size = sizing.iconSm,
                    tint = if (enabled) content.iconInactive else content.iconDisabled,
                    contentDescription = null,
                    minimumStroke = sizing.iconStrokeLight,
                )
            }
        }
    }
}

/**
 * Shorter than a standalone chip, and under the touch minimum on purpose.
 *
 * These stack inside a field: at full chip height two rows of them make the field taller than a
 * button, and a requisition with six materials would push the fields below it off the screen. The
 * chip body is not the target — the X inside it is, and that is sized properly. Making the whole
 * chip tappable at 48dp would be worse than useless here, because the only thing tapping a chip can
 * do is delete it.
 */
private val ChipMinHeight = 24.dp
