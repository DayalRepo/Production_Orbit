package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * What the grid should highlight.
 *
 * A sealed interface around a single case, which is on purpose. It carried a second case for the
 * start-and-target picker; that picker is gone and the shape is kept because it is the seam a future
 * selection mode arrives on, and widening a sealed type is a change the compiler walks you through
 * whereas widening a bare nullable date is one it lets you get wrong.
 */
@Immutable
sealed interface OrbitCalendarSelection {

    /** One day, for a target-date picker. */
    @Immutable
    data class Single(val date: OrbitCalendarDate?) : OrbitCalendarSelection
}

/**
 * A single month as a 7-column grid.
 *
 * ### Why the cells are not glass
 *
 * The panel around this grid is glass and the endpoint markers are, but the 42 day cells are plain.
 * They are a dense grid of small tap targets, and a gradient plus a rim on each one turns a calendar
 * into a sheet of buttons — the numbers stop being the figure and the boxes start being it. The same
 * reasoning the design system applies to list rows.
 *
 * @param month the month on display, which is independent of what is selected.
 * @param bounds the selectable window. Days outside it are struck through and inert.
 * @param selection what to highlight.
 * @param onDayClick fired only for selectable days; unselectable ones are not clickable at all rather
 *   than clickable-and-ignored, so a screen reader does not announce them as buttons.
 * @param daySize the diameter of the selection marker, and so the visual density of the grid. This is
 *   the knob that sizes the calendar: seven of these plus the panel's padding is the width, six rows
 *   of [rowHeight] is the height. It does **not** set the tap area — see [rowHeight].
 * @param rowHeight the height of a week row, and the tap height of every day in it. Floored at the
 *   platform minimum touch target rather than at [daySize], because a 32dp marker is a perfectly
 *   legible dot and a 32dp *target* fails WCAG — the visible circle and the thing your thumb has to
 *   hit are two different measurements, and only one of them is a design decision.
 */
@Composable
fun OrbitCalendarGrid(
    month: OrbitYearMonth,
    bounds: OrbitCalendarBounds,
    selection: OrbitCalendarSelection,
    onDayClick: (OrbitCalendarDate) -> Unit,
    modifier: Modifier = Modifier,
    daySize: Dp = OrbitTheme.sizing.iconXl,
    rowHeight: Dp = maxOf(daySize, OrbitTheme.sizing.minTouchTarget),
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OrbitWeekdayLabels.forEach { label ->
                Text(
                    text = label,
                    style = OrbitTheme.typography.labelMedium,
                    // Tertiary, and the only place in the grid that uses it. A weekday caption is a
                    // ruler for the columns beneath it, not data — at secondary it competed with the
                    // numbers for attention every time the eye crossed the header.
                    color = content.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(modifier = Modifier.height(spacing.sm))

        // Leading blanks push the 1st under its weekday. Rendering empty cells rather than the
        // previous month's trailing days keeps every number on screen selectable, so there is no
        // greyed-out digit inviting a tap that does nothing.
        val leadingBlanks = month.firstDayOfWeek
        val totalCells = leadingBlanks + month.lengthInDays
        val rows = (totalCells + DaysPerWeek - 1) / DaysPerWeek

        repeat(rows) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(DaysPerWeek) { column ->
                    val cellIndex = rowIndex * DaysPerWeek + column
                    val dayNumber = cellIndex - leadingBlanks + 1

                    if (dayNumber < 1 || dayNumber > month.lengthInDays) {
                        Box(modifier = Modifier.weight(1f).height(rowHeight))
                    } else {
                        DayCell(
                            date = month.day(dayNumber),
                            month = month,
                            bounds = bounds,
                            selection = selection,
                            daySize = daySize,
                            rowHeight = rowHeight,
                            onClick = onDayClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayCell(
    date: OrbitCalendarDate,
    month: OrbitYearMonth,
    bounds: OrbitCalendarBounds,
    selection: OrbitCalendarSelection,
    daySize: Dp,
    rowHeight: Dp,
    onClick: (OrbitCalendarDate) -> Unit,
) {
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors

    val selectable = bounds.isSelectable(date)
    val isToday = date == bounds.today

    val endpoint = when (selection) {
        is OrbitCalendarSelection.Single -> date == selection.date
    }

    // One source per cell, so the press effect is bounded to the day the finger is on. Feeding all
    // 42 cells from a shared source would light up the whole grid on every tap.
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .weight(1f)
            // The row's full height is the target, so the tappable area of a day is the whole cell
            // rather than the circle drawn in the middle of it.
            .height(rowHeight)
            .then(
                if (selectable) {
                    Modifier
                        .orbitHandCursor()
                        .clickable(
                            interactionSource = interactionSource,
                            // Suppressed here and re-attached below, clipped to the marker. Left to
                            // draw on this node the ripple would fill the full rectangular cell and
                            // spill into its neighbours, so tapping the 8th would wash colour under
                            // the 7th and 9th.
                            indication = null,
                            role = Role.Button,
                        ) { onClick(date) }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        // The marker and its number sit inside the press effect, because on iOS that effect is a
        // shrink of whatever it wraps and these two *are* the day. Clipped to the marker so the
        // Android ripple is a circle on the day rather than a rectangle across the cell.
        Box(
            modifier = Modifier
                .size(daySize)
                .clip(MarkerShape)
                .indication(interactionSource, orbitPressIndication()),
            contentAlignment = Alignment.Center,
        ) {
            // Layer 2: the endpoint marker, or today's ring. Glass here, because a marker is a badge-
            // sized pill and this is exactly the object the badge treatment was tuned for.
            if (endpoint) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .orbitGlass(
                            fill = control.actionContainer,
                            shape = MarkerShape,
                            highlightAlpha = if (OrbitTheme.isDark) {
                                OrbitGlass.BadgeHighlightDark
                            } else {
                                OrbitGlass.BadgeHighlightLight
                            },
                        ),
                )
            } else if (isToday) {
                // A ring rather than a fill: today is a landmark for orientation, not a selection, and
                // filling it makes the calendar open looking as though a choice had already been made.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = OrbitTheme.sizing.hairline,
                            color = content.textTertiary,
                            shape = MarkerShape,
                        ),
                )
            }

            // Layer 3: the number, always on top and always opaque.
            Text(
                text = date.day.toString(),
                style = OrbitTheme.typography.bodyMedium,
                fontWeight = if (endpoint || isToday) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    endpoint -> control.onActionContainer
                    !selectable -> content.textDisabled
                    else -> content.textPrimary
                },
                // Struck through rather than merely dimmed, as in the reference. Dimming alone is read
                // as "less important"; a strike is read as "not available", which is the actual
                // meaning.
                textDecoration = if (selectable) null else TextDecoration.LineThrough,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private val MarkerShape = RoundedCornerShape(percent = 50)

private const val DaysPerWeek = 7
