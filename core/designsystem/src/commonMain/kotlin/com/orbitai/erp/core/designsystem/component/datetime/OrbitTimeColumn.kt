package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.orbitai.erp.core.designsystem.component.container.OrbitLazyScrollbar
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors
import androidx.compose.ui.semantics.Role

/**
 * The scrolling list of time slots beside a calendar.
 *
 * ### Why a list rather than a spinner or two dropdowns
 *
 * An hour wheel plus a minute wheel plus an AM/PM toggle is three gestures to express one thought,
 * and it lets the user build a time that is not on offer — 10:07 on a site that books in quarter
 * hours. A list of the slots that actually exist is one tap, cannot produce an invalid answer, and
 * makes availability visible rather than something discovered on submit.
 *
 * The cost is height, which is why this scrolls and why it auto-scrolls to the selection: a list of
 * 37 slots opening at 09:00 hides a 4 PM choice the user already made.
 *
 * @param slots what is on offer. Generate with [OrbitTimeOfDay.slots] or take the working-day default.
 * @param unavailable slots that exist but cannot be taken — already booked, outside a shift. Shown
 *   struck through rather than omitted, so the list does not silently change length between two days
 *   and leave the user wondering whether they misread it.
 */
@Composable
fun OrbitTimeColumn(
    slots: List<OrbitTimeOfDay>,
    selected: OrbitTimeOfDay?,
    onSelect: (OrbitTimeOfDay) -> Unit,
    modifier: Modifier = Modifier,
    unavailable: Set<OrbitTimeOfDay> = emptySet(),
) {
    val spacing = OrbitTheme.spacing
    val listState = rememberLazyListState()

    // Bring the chosen slot into view when the panel opens or the date changes. Keyed on the value
    // rather than the index so it re-runs when a new date offers a different set of slots.
    LaunchedEffect(selected, slots) {
        val index = slots.indexOf(selected)
        if (index >= 0) {
            // Two rows above the target rather than flush to the top edge, so the slot arrives with
            // its neighbours visible and reads as a position in a list instead of as the first item.
            listState.scrollToItem(index = maxOf(0, index - ContextRows))
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        items(slots) { slot ->
            TimeSlotRow(
                slot = slot,
                selected = slot == selected,
                enabled = slot !in unavailable,
                onClick = { onSelect(slot) },
            )
        }
    }
}

/**
 * The same slots laid out along the x-axis, for when the panel is too narrow to hold a column.
 *
 * ### Why the axis changes rather than the column just getting narrower
 *
 * A vertical list under a calendar, inside a screen that itself scrolls vertically, puts two scroll
 * containers on the same axis — and the inner one swallows the drag, so the user cannot scroll past
 * the picker. Turning the list on its side removes the conflict outright instead of managing it with
 * nested-scroll plumbing.
 *
 * It also costs almost no height, which is the resource a phone is short of once a month grid has
 * taken 240dp of it.
 */
@Composable
fun OrbitTimeRow(
    slots: List<OrbitTimeOfDay>,
    selected: OrbitTimeOfDay?,
    onSelect: (OrbitTimeOfDay) -> Unit,
    modifier: Modifier = Modifier,
    unavailable: Set<OrbitTimeOfDay> = emptySet(),
) {
    val spacing = OrbitTheme.spacing
    val listState = rememberLazyListState()

    LaunchedEffect(selected, slots) {
        val index = slots.indexOf(selected)
        if (index >= 0) listState.scrollToItem(index = maxOf(0, index - ContextRows))
    }

    Column(modifier = modifier.padding(top = spacing.sm)) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            items(slots) { slot ->
                TimeSlot(
                    slot = slot,
                    selected = slot == selected,
                    enabled = slot !in unavailable,
                    onClick = { onSelect(slot) },
                    // Sized to its label rather than stretched, since a row has no single width to fill.
                    modifier = Modifier.width(SlotWidth),
                )
            }
        }

        OrbitLazyScrollbar(
            listState = listState,
            horizontal = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = spacing.md),
        )
    }
}

@Composable
private fun TimeSlotRow(
    slot: OrbitTimeOfDay,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) = TimeSlot(
    slot = slot,
    selected = selected,
    enabled = enabled,
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
)

@Composable
private fun TimeSlot(
    slot: OrbitTimeOfDay,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.button

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(OrbitTheme.sizing.buttonHeightMd)
            // Clip then indicate, both *before* the glass, and the order is the whole point. The
            // iOS effect is a shrink of everything the node draws, so it has to sit above the glass
            // in the chain to take the pill's fill and rim down with the label — below it, the label
            // would dip inside a fill that stayed put. The clip bounds the Android ripple to the
            // pill instead of letting it square off the corners.
            .clip(shape)
            .indication(interactionSource, orbitPressIndication())
            // Glass on each slot, which the design system's own rule about dense lists would normally
            // discourage — but `orbitGlass` is two gradients and a rim with no backdrop blur, so it
            // costs no more per row than a solid fill would. The rule exists for real blurs.
            .orbitGlass(
                fill = if (selected) control.actionContainer else control.cardContainer,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.BadgeHighlightDark
                } else {
                    OrbitGlass.BadgeHighlightLight
                },
                edge = if (selected) null else control.controlBorder,
                edgeWidth = OrbitTheme.sizing.hairline,
            )
            .then(
                if (enabled) {
                    Modifier.orbitHandCursor().clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = slot.format12Hour(),
            style = OrbitTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = when {
                selected -> control.onActionContainer
                !enabled -> content.textDisabled
                else -> content.textPrimary
            },
            textDecoration = if (enabled) null else androidx.compose.ui.text.style.TextDecoration.LineThrough,
        )
    }
}

/** Rows of context kept before an auto-scrolled selection. */
private const val ContextRows = 2

/** Fits `12:00 PM` at body size with its padding, which is the widest label the list can hold. */
private val SlotWidth = 96.dp
