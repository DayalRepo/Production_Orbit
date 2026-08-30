package com.orbitai.erp.core.designsystem.component.status

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.controlColors

/**
 * A selectable filter chip, for the filter rows above task, issue and material lists.
 *
 * The visible pill is [OrbitSizing.chipHeight] tall but the touch target is expanded to
 * [OrbitSizing.minTouchTarget] — 48dp on Android, 44pt on iOS — because a Site Engineer may be
 * tapping this with gloves on. A row of chips therefore occupies the full touch-target height even
 * though the pills look 36dp tall.
 *
 * Press feedback is platform-native — a ripple on Android, a UIKit-style shrink on iOS — and a hand
 * cursor appears wherever a pointer is attached. See `orbitPressIndication`.
 *
 * Chips are text and number only. A glyph would have to be the same glyph on every chip in a row —
 * they all filter the same kind of thing — and a column of identical icons carries no information
 * while costing the width the label needs. The count is the useful signal, because "Blocked 2" tells
 * you whether the filter is worth tapping before you tap it.
 *
 * @param count optional trailing count, e.g. the number of matches behind this filter.
 */
@Composable
fun OrbitChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    count: Int? = null,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val control = OrbitTheme.controlColors
    val shape = OrbitTheme.shapeTokens.chip

    // The monochrome control palette, not the accent one. A chip is a control, and it sits in rows
    // directly above lists whose badges are the accent colours — a blue selected chip above a row
    // of blue "In progress" badges reads as a badge that has grown, and the one meaning colour
    // carries in this app is status. Selection is signalled by inversion instead, which is the same
    // move the buttons make and cannot be confused with a state.
    val background = if (selected) control.actionContainer else control.controlContainer
    val foreground = if (selected) control.onActionContainer else control.controlContent
    val borderColor = if (selected) Color.Transparent else control.controlBorder

    val alpha = if (enabled) 1f else OrbitAlpha.Disabled
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            // Expanded here rather than on a wrapper Box, so that a caller passing a width widens
            // the chip itself instead of centring it inside a wider invisible parent.
            .minimumInteractiveComponentSize()
            // heightIn, not height: at 200% font scale (WCAG 1.4.4) the label must be able to push
            // the pill taller instead of being clipped by it.
            .heightIn(min = sizing.chipHeight)
            // Before the clip, and passed the same alpha the fill gets, so a dimmed chip settles onto
            // the page instead of hovering over it at full depth.
            .orbitGlassShadow(shape = shape, elevation = sizing.shadowBadge, alpha = alpha)
            .clip(shape)
            .background(background.copy(alpha = background.alpha * alpha), shape)
            .border(sizing.border, borderColor.copy(alpha = borderColor.alpha * alpha), shape)
            .orbitHandCursor()
            .selectable(
                selected = selected,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = orbitPressIndication(),
                onClick = onClick,
            )
            .padding(horizontal = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingContent?.invoke()
        Text(
            text = label,
            style = OrbitTheme.typography.labelLarge,
            color = foreground.copy(alpha = foreground.alpha * alpha),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (count != null) {
            Text(
                text = count.toString(),
                style = OrbitTheme.typography.labelMedium,
                color = foreground.copy(alpha = foreground.alpha * alpha * CountAlpha),
            )
        }
    }
}

/** The count is secondary to the label, so it sits back slightly even when selected. */
private const val CountAlpha = 0.75f
