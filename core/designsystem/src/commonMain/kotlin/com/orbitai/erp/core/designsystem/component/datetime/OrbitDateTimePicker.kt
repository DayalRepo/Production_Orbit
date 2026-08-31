package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.orbitai.erp.core.designsystem.component.button.OrbitButton
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonSize
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonState
import com.orbitai.erp.core.designsystem.component.button.OrbitButtonVariant
import com.orbitai.erp.core.designsystem.component.container.OrbitDivider
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A calendar and a time list, for choosing one moment.
 *
 * ### The panel is the glass, the contents are not
 *
 * `orbitGlassShadow` plus `orbitGlass` on the outer column, and then the grid cells inside are plain
 * fills. That split is the whole reason the panel reads as a floating object rather than as a
 * decorated region of the page: the shadow says where it is, the rim and highlight say what it is made
 * of, and the contents stay legible because nothing behind them is competing.
 *
 * The two things inside that *are* glass are the selected-day marker and the time slots, both of which
 * are badge-sized pills — the object the badge treatment was tuned against.
 *
 * ### Confirmation is explicit
 *
 * Choosing a day does not commit. The footer restates the full selection as a sentence and the user
 * presses Confirm, which matters because a date and a time are two separate taps: committing on the
 * second one means a user who picks the time first and then changes the date has already submitted
 * something they did not mean. The footer is also the only place the choice appears in prose, which is
 * what makes "12/06/2025" checkable — day-first and month-first slashed dates are indistinguishable
 * for the first twelve days of any month, and "12 Jun 2025" is not.
 *
 * @param bounds the selectable window; supply today from the calling module's clock.
 * @param selection the committed value, or null when nothing has been chosen.
 * @param onConfirm fired with a complete date *and* time. Never called with a half-selection.
 * @param slots the times on offer. Defaults to a 09:00-18:00 working day in quarter hours.
 * @param daySize diameter of a day marker. The tap target stays at the platform minimum anyway.
 */
@Composable
fun OrbitDateTimePicker(
    bounds: OrbitCalendarBounds,
    selection: OrbitDateTimeSelection?,
    onConfirm: (OrbitDateTimeSelection) -> Unit,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    slots: List<OrbitTimeOfDay> = OrbitTimeOfDay.workingDay(),
    confirmLabel: String = "Schedule",
    daySize: Dp = OrbitTheme.sizing.iconXl,
) {
    // Draft state, so the panel can be half-filled without the caller seeing an invalid value. The
    // committed `selection` seeds it and is otherwise untouched until Confirm.
    var draftDate by remember(selection) { mutableStateOf(selection?.date) }
    var draftTime by remember(selection) { mutableStateOf(selection?.time) }
    var month by remember(selection) {
        mutableStateOf(selection?.date?.yearMonth ?: bounds.today.yearMonth)
    }

    PickerPanel(modifier = modifier) {
        CalendarBody(
            month = month,
            bounds = bounds,
            selection = OrbitCalendarSelection.Single(draftDate),
            daySize = daySize,
            onMonthChange = { month = it },
            onDayClick = { draftDate = it },
        )

        // Time under the calendar rather than beside it. A column of slots to the right is the
        // desktop arrangement the references show, and it costs a third of the panel's width — which
        // on a phone is taken straight out of the day cells, the one thing in here that has to stay
        // thumb-sized. Along the x-axis the slots cost almost no height and stay full size.
        PanelRule()
        OrbitTimeRow(
            slots = slots,
            selected = draftTime,
            onSelect = { draftTime = it },
            modifier = Modifier.fillMaxWidth(),
        )

        PickerFooter(
            value = draftDate?.let { date ->
                draftTime?.let { time -> OrbitDateTimeSelection(date, time).format() }
            },
            placeholder = when {
                draftDate == null -> "Select a date"
                else -> "Select a time"
            },
            confirmLabel = confirmLabel,
            confirmEnabled = draftDate != null && draftTime != null,
            onCancel = onCancel,
            onConfirm = {
                val date = draftDate ?: return@PickerFooter
                val time = draftTime ?: return@PickerFooter
                onConfirm(OrbitDateTimeSelection(date, time))
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
 * The value the picker is about to commit, and the pair of buttons that decide its fate.
 *
 * ```
 *  ------------------------------------
 * | [cal] 21/09/2027 · 10:00 AM        |
 *  ------------------------------------
 *  [Cancel]                [Schedule] 
 * ```
 *
 * ### Why the value gets its own row instead of sharing one with the buttons
 *
 * It shared a row in the first pass, as a sentence to the left of the actions. Two problems. The
 * sentence is the longest string in the panel and the buttons are the widest fixed objects, so on a
 * phone the text wrapped to two lines and shoved the buttons out of vertical alignment with it. More
 * importantly, prose beside a button reads as *explanation* of the button. This string is not
 * commentary — it is the value being committed, the last thing a user checks before pressing
 * Schedule, and it deserves to be the thing directly above the button rather than beside it.
 *
 * Boxed in the inset fill and carrying the calendar glyph, so it reads as a *field showing a value* —
 * the same object as the `OrbitDateTimeField` that opened the panel. That echo is deliberate: what
 * you see in the box is exactly what will appear in the field when you commit.
 *
 * ### The buttons split the width evenly
 *
 * Both take `weight(1f)`, so they are the same width and the same height and together they span the
 * panel under a rule of their own. This replaced a `SpaceBetween` pair sized to their own labels,
 * which had two problems: `Cancel` and `Schedule` are different lengths, so two buttons of visibly
 * different widths read as different *kinds* of control — one primary, one incidental — when they are
 * two outcomes of equal standing; and sized to their text they were small targets with a wide dead
 * gap between them, in the part of the panel a thumb actually lands.
 *
 * Splitting the width keeps what `SpaceBetween` was for — the two are still at opposite ends, so
 * neither is where a thumb aiming for the other will land — while making them equal and large. The
 * rule above them separates the decision from the value it applies to, which is the same job the rule
 * above the value box does for the calendar.
 *
 * @param value the formatted selection, or null while it is incomplete.
 * @param placeholder what to show instead, naming the step still outstanding.
 */
@Composable
private fun PickerFooter(
    value: String?,
    placeholder: String,
    confirmLabel: String,
    confirmEnabled: Boolean,
    onCancel: (() -> Unit)?,
    onConfirm: () -> Unit,
    trailing: String? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val content = OrbitTheme.contentColors

    PanelRule()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = sizing.fieldHeightMd)
            .orbitGlass(
                fill = control.insetContainer,
                shape = OrbitTheme.shapeTokens.field,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.SurfaceHighlightDark
                } else {
                    OrbitGlass.SurfaceHighlightLight
                },
                edge = control.controlBorder,
                edgeWidth = sizing.hairline,
            )
            .padding(horizontal = sizing.fieldPaddingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OrbitGlyph(
            icon = OrbitIcons.CalendarDate,
            size = sizing.iconSm,
            tint = if (value != null) content.iconPrimary else content.iconInactive,
            contentDescription = null,
        )
        Text(
            text = value ?: placeholder,
            style = OrbitTheme.typography.bodyLarge,
            // Weight is what distinguishes a value from a prompt here, the same way it does in the
            // field this box mirrors.
            fontWeight = if (value != null) FontWeight.SemiBold else FontWeight.Normal,
            color = if (value != null) content.textPrimary else content.textTertiary,
            maxLines = 1,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = OrbitTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = content.textSecondary,
                maxLines = 1,
            )
        }
    }

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

/** A rule with the panel's standard breathing room above and below it. */
@Composable
private fun PanelRule() {
    val spacing = OrbitTheme.spacing
    Box(modifier = Modifier.height(spacing.md))
    OrbitDivider()
    Box(modifier = Modifier.height(spacing.md))
}
