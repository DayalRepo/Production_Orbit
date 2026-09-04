package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * A field that opens a searchable list and puts what you picked back in the field.
 *
 * ```
 *  ------------------------------
 * |  Slab Concreting          v  |
 *  ------------------------------
 * |  [ Search stages          ]  |
 * |  [+] Add stage               |
 * |==============================|
 * |  Block Work                  |
 * |  ---------------             |
 * |  Slab Concreting          v  |
 *  ------------------------------
 * ```
 *
 * ### It is a field, not a button that happens to show text
 *
 * It borrows [OrbitFieldShell] whole — the same rim, the same focus behaviour, the same heights and
 * the same error and success states as [OrbitTextField]. That matters more than it sounds: forms in
 * this product mix typed and chosen values in the same column, and a picker built as a button sits a
 * couple of pixels off every text field beside it, with a rim that does not respond when the rest of
 * the form shows focus. Users read that misalignment as "this one is different" and hesitate over
 * it.
 *
 * So the selected value is set in the same ink and weight as typed text, and the placeholder in the
 * same lighter ink as a text field's hint. A chosen value and a typed value are both answers.
 *
 * ### Search is built in, because the lists here are long
 *
 * The vocabulary this was built for — the stages on a construction work sequence — runs to about a
 * hundred entries, and a hundred entries is past the point where scrolling is a reasonable way to
 * find one. Filtering is a plain case-insensitive substring match on the whole label rather than a
 * prefix match, because the distinguishing word in these names is usually not the first one: someone
 * looking for "Toilet Dado Tiling" is at least as likely to type "tiling" or "dado" as "toilet".
 *
 * The query is held here and cleared when the panel closes, so reopening the field always shows the
 * full list. A filter that persisted invisibly between openings is a field that appears to have lost
 * most of its options.
 *
 * ### The chevron rotates rather than swapping
 *
 * Swapping a down glyph for an up glyph lands the same two states without saying anything about how
 * the panel got there. Rotating carries the motion: the chevron turns over the same interval the
 * list takes to open, so one continuous movement connects the control the user touched to the panel
 * that appeared somewhere else on screen.
 *
 * @param selected the current value, or null for none. The placeholder shows in its place.
 * @param onAddRequest null to leave the "add" row out. Non-null for a vocabulary the user is allowed
 *   to extend — the component only asks; creating the entry, persisting it and deciding whether it
 *   was a duplicate all belong to the caller, which is the only party holding the list.
 * @param label spoken, not drawn — the same convention as every other field here. A form's visible
 *   labels are the form's business, and a field that draws its own sits at a different height from
 *   its neighbours the moment the form uses any other layout.
 */
@Composable
fun OrbitDropdownField(
    selected: String?,
    options: List<String>,
    onSelect: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Select",
    searchPlaceholder: String = "Search",
    addLabel: String? = null,
    onAddRequest: (() -> Unit)? = null,
    size: OrbitFieldSize = OrbitFieldSize.Medium,
    state: OrbitFieldState = OrbitFieldState.Default,
    enabled: Boolean = true,
    /** Override chevron size; defaults follow [size]. */
    iconSize: Dp? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val interactionSource = remember { MutableInteractionSource() }

    var expanded by rememberDropdownExpanded()
    var query by remember { mutableStateOf("") }

    // The anchor's measured width, handed to the panel so the list comes out the same width as the
    // field. Read off the laid-out control rather than derived from the caller's modifier, because
    // the field is usually stretched by its parent and the caller does not know the number either.
    val density = LocalDensity.current
    var anchorWidth by remember { mutableStateOf(0.dp) }

    val minHeight = size.pick(sizing.fieldHeightSm, sizing.fieldHeightMd, sizing.fieldHeightLg)
    val padding = size.pick(sizing.fieldPaddingSm, sizing.fieldPaddingMd, sizing.fieldPaddingLg)
    val chevronSize = iconSize ?: size.pick(sizing.iconSm, sizing.iconSm, sizing.iconMd)

    val base: TextStyle = size.pick(
        OrbitTheme.typography.bodyMedium,
        OrbitTheme.typography.bodyLarge,
        OrbitTheme.extendedTypography.fieldLarge,
    )

    val ink = if (enabled) content.textPrimary else content.textPrimary.copy(OrbitAlpha.Disabled)
    val hint = if (enabled) content.textSecondary else content.textSecondary.copy(OrbitAlpha.Disabled)

    val rotation by animateFloatAsState(
        // 180, not -180. Both land upside down, and the sign decides which way the glyph sweeps —
        // turning clockwise to open and anticlockwise to close reads as one control being wound and
        // unwound, where matching directions would read as it spinning.
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(OrbitDropdownOpenMs),
        label = "orbit-dropdown-chevron",
    )

    val visible = options.filterByQuery(query)

    fun close() {
        expanded = false
        // Dropped on close, so the field never reopens showing a filtered list with no visible
        // explanation of why most of its options are missing.
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
                    // Announced as a dropdown rather than a button, so assistive technology offers
                    // "expand" instead of a bare activation and reads the current value as the
                    // field's own rather than as a stray label floating beside it.
                    role = Role.DropdownList,
                ) { if (expanded) close() else expanded = true }
                .semantics { contentDescription = "$label, ${selected ?: placeholder}" },
        ) {
            Text(
                text = selected ?: placeholder,
                style = base,
                fontWeight = FontWeight.Medium,
                color = if (selected != null) ink else hint,
                modifier = Modifier
                    .weight(1f)
                    .clearAndSetSemantics {},
            )

            OrbitGlyph(
                icon = OrbitIcons.ChevronDown,
                size = chevronSize,
                tint = if (enabled) content.iconInactive else content.iconInactive.copy(OrbitAlpha.Disabled),
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
                            // Closing first. The dialog it opens is a second layer over the same
                            // screen, and a dropdown left open underneath it both steals the back
                            // gesture and covers the dialog's own dismiss area.
                            close()
                            it()
                        }
                    },
                )
            },
        ) {
            visible.forEach { option ->
                OrbitDropdownRow(
                    label = option,
                    selected = option == selected,
                    onClick = {
                        onSelect(option)
                        // Single select has exactly one answer, so the panel has done its job the
                        // moment one is given. Staying open would leave the user to dismiss a list
                        // whose remaining rows can only undo what they just did.
                        close()
                    },
                )
            }

            if (visible.isEmpty()) {
                OrbitDropdownEmptyRow(query = query)
            }
        }
    }
}

/**
 * Case-insensitive substring match on the whole label.
 *
 * Substring rather than prefix because the distinguishing word in a work-sequence stage is rarely
 * the first one, and rarely one the user could guess: "Internal Other Area Plastering (Staircase
 * except Lift Door Wall)" is found by typing "staircase", and a prefix match would return nothing.
 */
internal fun List<String>.filterByQuery(query: String): List<String> {
    val needle = query.trim()
    if (needle.isEmpty()) return this
    return filter { it.contains(needle, ignoreCase = true) }
}

/**
 * What the list shows when the filter matches nothing.
 *
 * An empty panel is ambiguous between "no matches" and "still loading", and a panel that collapses
 * to nothing at all moves the add row out from under the finger that is on its way to it. Saying so
 * in words costs one row and removes the question — and it names the query back, so a user who has
 * fat-fingered the search can see it.
 */
@Composable
private fun OrbitDropdownEmptyRow(query: String) {
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
