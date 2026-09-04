package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A calendar for choosing a start day and an end day.
 *
 * ### The panel is the glass, the contents are not
 *
 * `orbitGlassShadow` plus `orbitGlass` on the outer column, and then the grid cells inside are plain
 * fills. That split is the whole reason the panel reads as a floating object rather than as a
 * decorated region of the page: the shadow says where it is, the rim and highlight say what it is made
 * of, and the contents stay legible because nothing behind them is competing.
 *
 * The selected-day markers are glass — badge-sized pills, the object the badge treatment was tuned
 * against. Days between them carry a rail so the span is readable without a second display of the
 * dates.
 *
 * ### Confirmation is explicit
 *
 * The first tap sets the start, the second sets the end (swapped if it falls before the start), and
 * a third tap begins a new span. None of that commits: the caller only hears about a complete range
 * when Confirm is pressed, so paging months or changing an endpoint cannot leak a half-selection
 * into the form.
 *
 * @param bounds the selectable window; supply today from the calling module's clock.
 * @param selection the committed span, or null when nothing has been chosen.
 * @param onConfirm fired with a complete start *and* end. Never called with a half-selection.
 * @param daySize diameter of a day marker. The tap target stays at the platform minimum anyway.
 */
@Composable
fun OrbitDateTimePicker(
    bounds: OrbitCalendarBounds,
    selection: OrbitDateRange?,
    onConfirm: (OrbitDateRange) -> Unit,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    confirmLabel: String = "Set dates",
    daySize: Dp = OrbitTheme.sizing.iconXl,
) {
    var draftStart by remember(selection) { mutableStateOf(selection?.start) }
    var draftEnd by remember(selection) { mutableStateOf(selection?.end) }
    var month by remember(selection) {
        mutableStateOf(selection?.start?.yearMonth ?: bounds.today.yearMonth)
    }

    PickerPanel(modifier = modifier) {
        CalendarBody(
            month = month,
            bounds = bounds,
            selection = OrbitCalendarSelection.Range(start = draftStart, end = draftEnd),
            daySize = daySize,
            onMonthChange = { month = it },
            onDayClick = { date ->
                val start = draftStart
                val end = draftEnd
                when {
                    start == null || end != null -> {
                        draftStart = date
                        draftEnd = null
                    }
                    date < start -> {
                        draftEnd = start
                        draftStart = date
                    }
                    else -> draftEnd = date
                }
            },
        )

        PickerFooter(
            confirmLabel = confirmLabel,
            confirmEnabled = draftStart != null && draftEnd != null,
            onCancel = onCancel,
            onConfirm = {
                val start = draftStart ?: return@PickerFooter
                val end = draftEnd ?: return@PickerFooter
                onConfirm(OrbitDateRange(start, end))
            },
        )
    }
}

/**
 * Navigator plus grid.
 *
 * Factored out of the picker so the header and the grid stay wired together in one place. It used to
 * also own the swap between the grid and a month/year chooser panel; the chooser is now two dropdowns
 * inside the navigator, which open *over* the calendar, so there is nothing left to swap and the
 * animated visibility either side of it went with it.
 */
@Composable
private fun ColumnScope.CalendarBody(
    month: OrbitYearMonth,
    bounds: OrbitCalendarBounds,
    selection: OrbitCalendarSelection,
    daySize: Dp,
    onMonthChange: (OrbitYearMonth) -> Unit,
    onDayClick: (OrbitCalendarDate) -> Unit,
) {
    OrbitMonthNavigator(
        month = month,
        bounds = bounds,
        onMonthChange = onMonthChange,
    )

    Box(modifier = Modifier.height(OrbitTheme.spacing.md))

    OrbitCalendarGrid(
        month = month,
        bounds = bounds,
        selection = selection,
        onDayClick = onDayClick,
        daySize = daySize,
    )
}

/** The glass shell both pickers sit in, so they cannot drift apart. */
@Composable
private fun PickerPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.card

    Column(
        modifier = modifier
            .orbitGlassShadow(shape = shape, elevation = OrbitTheme.sizing.shadowOverlay)
            .orbitGlass(
                fill = control.cardContainer,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.SurfaceHighlightDark
                } else {
                    OrbitGlass.SurfaceHighlightLight
                },
                edge = control.controlBorder,
                edgeWidth = OrbitTheme.sizing.hairline,
            )
            .padding(OrbitTheme.spacing.md),
        content = content,
    )
}

/**
 * Cancel and confirm under the calendar.
 *
 * Both take `weight(1f)`, so they are the same width and the same height and together they span the
 * panel. Splitting the width keeps them at opposite ends — neither is where a thumb aiming for the
 * other will land — while making them equal and large.
 */
@Composable
private fun PickerFooter(
    confirmLabel: String,
    confirmEnabled: Boolean,
    onCancel: (() -> Unit)?,
    onConfirm: () -> Unit,
) {
    val spacing = OrbitTheme.spacing

    PanelRule()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        // Rendered even with no handler, as a disabled control, so the pair keeps its shape. Dropping
        // the left button would slide Schedule across to where Cancel had been — the one place a
        // returning user's thumb has learned not to press by accident.
        OrbitButton(
            label = "Cancel",
            onClick = onCancel ?: {},
            // Destructive, not Secondary, so the red already tuned for Reject carries the discard
            // here too — and it brings the variant's glass and shadow with it rather than needing
            // either restated. Red is right on the strict reading that this throws away what the
            // person just picked; a grey Cancel beside a filled Schedule also left the pair looking
            // like one button and one label.
            variant = OrbitButtonVariant.Destructive,
            size = OrbitButtonSize.Small,
            icon = OrbitIcons.Cancel,
            state = if (onCancel != null) OrbitButtonState.Active else OrbitButtonState.Disabled,
            modifier = Modifier.weight(1f),
        )

        OrbitButton(
            label = confirmLabel,
            onClick = onConfirm,
            variant = OrbitButtonVariant.Primary,
            size = OrbitButtonSize.Small,
            icon = OrbitIcons.CalendarSchedule,
            state = if (confirmEnabled) OrbitButtonState.Active else OrbitButtonState.Disabled,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * A rule with the panel's breathing room above and below it.
 *
 * Given the panel's own ink rather than the default divider colour, for the same reason the popover
 * cards were: this is an elevated white surface, and `dividerSubtle` on white is nothing.
 */
@Composable
private fun PanelRule() {
    val spacing = OrbitTheme.spacing
    Box(modifier = Modifier.height(spacing.lg))
    OrbitDivider(color = OrbitTheme.controlColors.dividerElevated)
    Box(modifier = Modifier.height(spacing.lg))
}
