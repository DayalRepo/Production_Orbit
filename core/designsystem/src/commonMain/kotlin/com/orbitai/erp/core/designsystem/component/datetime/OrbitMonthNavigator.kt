package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButton
import com.orbitai.erp.core.designsystem.component.button.OrbitIconButtonSize
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownMenu
import com.orbitai.erp.core.designsystem.component.input.OrbitDropdownRow
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * The header above a month grid: back, a month dropdown, a year dropdown, forward.
 *
 * ### Why two dropdowns and not a chooser panel
 *
 * Tapping the month's name used to swap the grid out for a panel of month pills and a row of year
 * pills. Two things were wrong with it. The panel replaced the calendar, so choosing a month meant
 * losing sight of the thing you were choosing it for, and the transition read as the component having
 * navigated somewhere rather than having opened a control. And the two units were being picked by the
 * same gesture in the same surface despite being different questions — with a twenty-year window the
 * year list is long and needs scrolling, while the month list is always exactly twelve.
 *
 * Separate dropdowns say what they are. Each carries its own chevron, opens over the calendar instead
 * of in place of it, and is the same object the rest of the product uses for "choose one of these" —
 * so the year list gets the scrolling and the tick-marked selection that [OrbitDropdownMenu] already
 * provides, and the month list keeps the compact form that worked.
 *
 * Paging arrows are kept either side. They are the right control for "the month after this one", which
 * is the common case and which a dropdown makes into two taps and a scan.
 */
@Composable
internal fun OrbitMonthNavigator(
    month: OrbitYearMonth,
    bounds: OrbitCalendarBounds,
    onMonthChange: (OrbitYearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing

    // Paging off either end of the selectable window is disabled rather than clamped. A clamped arrow
    // still looks pressable and does nothing, which reads as the app having missed the tap.
    val canGoBack = !bounds.isEntirelyPast(month.plusMonths(-1))
    val canGoForward = !bounds.isBeyondEnd(month.plusMonths(1))

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        OrbitIconButton(
            icon = OrbitIcons.ArrowRight,
            contentDescription = "Previous month",
            onClick = { onMonthChange(month.plusMonths(-1)) },
            state = if (canGoBack) OrbitButtonState.Active else OrbitButtonState.Disabled,
            size = OrbitIconButtonSize.Small,
            // Mirrored rather than a second generated asset. `OrbitIcons` comes from a fixed map in
            // `tools/gen_designsystem.py`, and regenerating it to add a left arrow would also rewrite
            // `BadgeColors.kt`, whose dark red tones were hand-tuned after generation.
            //
            // Flipping the whole button rather than just the glyph, since the component exposes no
            // handle on its icon — which is safe here and would not be on most buttons: the ring is a
            // circle and its glass highlight is a *vertical* gradient, so both are unchanged by a
            // horizontal mirror. The glyph is the only asymmetric thing inside.
            modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
        )

        // The two dropdowns share the middle. Weighted rather than sized to their labels so the
        // arrows stay pinned to the panel's edges and the month name does not shift the layout as it
        // changes between "May" and "September".
        //
        // The month is given more of the width than the year because its labels are longer and vary;
        // "2029" is four digits whatever the year.
        MonthYearDropdown(
            label = OrbitMonthNames.full(month.month),
            unitLabel = "Month",
            options = (1..12).map { OrbitMonthNames.full(it) },
            selectedIndex = month.month - 1,
            enabled = { index ->
                val candidate = OrbitYearMonth(month.year, index + 1)
                !bounds.isEntirelyPast(candidate) && !bounds.isBeyondEnd(candidate)
            },
            onSelect = { index -> onMonthChange(OrbitYearMonth(month.year, index + 1)) },
            modifier = Modifier.weight(MonthWeight),
        )

        val years = remember(bounds) {
            (bounds.today.year..bounds.lastSelectableYear).toList()
        }

        MonthYearDropdown(
            label = month.year.toString(),
            unitLabel = "Year",
            options = years.map { it.toString() },
            selectedIndex = years.indexOf(month.year),
            enabled = { true },
            onSelect = { index ->
                // Clamp the month when the year changes, so picking the current year cannot leave the
                // calendar sitting on a month that has already passed.
                val candidate = OrbitYearMonth(years[index], month.month)
                onMonthChange(
                    if (bounds.isEntirelyPast(candidate)) bounds.today.yearMonth else candidate,
                )
            },
            modifier = Modifier.weight(YearWeight),
        )

        OrbitIconButton(
            icon = OrbitIcons.ArrowRight,
            contentDescription = "Next month",
            onClick = { onMonthChange(month.plusMonths(1)) },
            state = if (canGoForward) OrbitButtonState.Active else OrbitButtonState.Disabled,
            size = OrbitIconButtonSize.Small,
        )
    }
}

/**
 * A compact pill that opens a list of options below itself.
 *
 * Not [com.orbitai.erp.core.designsystem.component.input.OrbitDropdownField], which is the right
 * control in a form and the wrong one here: it carries a caption above itself, stands at full field
 * height, and offers a search box and an "add" row. Inside a calendar header, between two paging
 * arrows, none of that fits — but the panel it drops is exactly what is wanted, so this reuses
 * [OrbitDropdownMenu] and [OrbitDropdownRow] and only replaces the trigger.
 *
 * @param unitLabel what this pill chooses — "Month" or "Year" — read to a screen reader ahead of
 *   the current value.
 * @param enabled per-option, so a month wholly in the past can be shown greyed rather than hidden.
 *   Hiding it would reorder the list, and a month list whose rows move is one nobody can aim at.
 */
@Composable
private fun MonthYearDropdown(
    label: String,
    unitLabel: String,
    options: List<String>,
    selectedIndex: Int,
    enabled: (Int) -> Boolean,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors
    val shape = RoundedCornerShape(percent = 50)

    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) ChevronFlipped else 0f,
        label = "orbit-monthyear-chevron",
    )

    // The trigger's measured width, handed to the panel so the list comes out under it at the same
    // width. Read off the laid-out pill rather than derived from the caller's modifier, because the
    // pill is weighted by its parent and neither it nor the caller knows the number in advance.
    val density = LocalDensity.current
    var anchorWidth by remember { mutableStateOf(0.dp) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = sizing.minTouchTarget)
                .onSizeChanged { anchorWidth = with(density) { it.width.toDp() } }
                // "Month: September" rather than just "September". Two pills sitting side by side
                // both read as a bare value otherwise, and which unit each one picks is exactly what
                // is obvious visually and absent to a screen reader.
                .semantics(mergeDescendants = true) {
                    contentDescription = "$unitLabel: $label"
                }
                // Clip and indicate above the glass, so the press takes the pill's fill and its label
                // down together. See the note on the time slot for why this order matters.
                .clip(shape)
                .indication(interactionSource, orbitPressIndication())
                .orbitGlass(
                    fill = control.insetContainer,
                    shape = shape,
                    highlightAlpha = if (OrbitTheme.isDark) {
                        OrbitGlass.BadgeHighlightDark
                    } else {
                        OrbitGlass.BadgeHighlightLight
                    },
                    edge = control.controlBorder,
                    edgeWidth = sizing.hairline,
                )
                .orbitHandCursor()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                ) { expanded = !expanded }
                .padding(horizontal = spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = OrbitTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = content.textPrimary,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            OrbitGlyph(
                icon = OrbitIcons.ChevronDown,
                size = sizing.iconSm,
                tint = content.iconInactive,
                // The label beside it already names the control, and the row's own description names
                // which unit it picks. A third reading here is noise.
                contentDescription = null,
                modifier = Modifier.graphicsLayer { rotationZ = chevronRotation },
            )
        }

        OrbitDropdownMenu(
            expanded = expanded,
            onDismiss = { expanded = false },
            // Floored, because a weighted pill in a calendar header can be narrower than a readable
            // list. "September" in a 90dp panel wraps or truncates; the panel is free to be wider
            // than the trigger that opened it, and here it has to be.
            width = maxOf(anchorWidth, DropdownMinWidth),
        ) {
            options.forEachIndexed { index, option ->
                OrbitDropdownRow(
                    label = option,
                    selected = index == selectedIndex,
                    enabled = enabled(index),
                    onClick = {
                        onSelect(index)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Half a turn: the chevron points down when the list is closed and up when it is open. */
private const val ChevronFlipped = 180f

/**
 * The month takes three fifths of the middle and the year two.
 *
 * Not an even split, because the labels are not evenly sized: the month runs to nine characters and
 * the year is always four. An even split leaves "September" crowded against its chevron while the
 * year pill sits half empty.
 */
private const val MonthWeight = 3f
private const val YearWeight = 2f

/**
 * Floor for the dropped list's width.
 *
 * Wide enough for the longest month at label size with the row's own padding, which is the binding
 * case — the year list would be content with far less.
 */
private val DropdownMinWidth = 160.dp
