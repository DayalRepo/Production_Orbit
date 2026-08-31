package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.foundation.orbitGlassShadow
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors

/**
 * The small numeric badge on a tab icon or an inbox row.
 *
 * ### The same glass as the status badges
 *
 * Tinted fill, hairline rim, specular top edge, contact shadow — the identical stack
 * `OrbitBadge` uses, on the identical `OrbitBadgeTone` palette. That matters more than it sounds:
 * a count badge and a status badge frequently sit in the same row, and a solid pill beside a glass
 * one reads as two components from two different products rather than as one system.
 *
 * The tones were tuned against the most elevated surface in each theme, so a badge on a card, a
 * sheet or a tab bar is inside what the generator budgeted for.
 *
 * **Where this is a real trade-off:** a translucent badge takes its colour from what is behind it,
 * and the classic placement for a count is overlapping a tab icon — the least predictable background
 * in the product. Over an icon's strokes the fill composites differently than over its negative
 * space. `CountBadgeContrastTest` therefore checks the digit and the fill over the *surface* a tab
 * bar actually uses, which is the honest common case; a badge placed directly on top of a dense
 * glyph should be nudged clear of it rather than relying on the fill to cover it, which is what the
 * shadow and the rim are there to make possible.
 *
 * ### The cap
 *
 * Counts above [MaxDisplayed] render as "99+". Not a stylistic choice: the badge is round at one
 * digit and a pill at two, and at four it is wider than the icon it belongs to, which pushes the tab
 * label around and makes the bar reflow every time a message arrives. The exact figure is not
 * something anyone acts on anyway — the difference between 100 unread and 340 unread is not a
 * difference in what you do next.
 *
 * The spoken description always carries the real number, because "99 plus" is a visual abbreviation
 * and a screen reader user should get the count the app actually knows.
 *
 * @param count items pending. Zero renders nothing at all rather than a "0" badge, since a badge
 *   showing nothing outstanding is a notification that there is no notification.
 * @param label what is being counted, for the spoken description: "unread messages", "open issues".
 */
@Composable
fun OrbitCountBadge(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    tone: OrbitBadgeTone = OrbitBadgeTone.Red,
) {
    if (count <= 0) return

    val sizing = OrbitTheme.sizing
    val spacing = OrbitTheme.spacing
    val palette = tone.colors

    val shown = if (count > MaxDisplayed) "$MaxDisplayed+" else "$count"

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = sizing.countBadgeMinSize, minHeight = sizing.countBadgeMinSize)
            .orbitGlassShadow(shape = CircleShape, elevation = sizing.shadowBadge)
            .orbitGlass(
                fill = palette.container,
                shape = CircleShape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.BadgeHighlightDark
                } else {
                    OrbitGlass.BadgeHighlightLight
                },
                // The tone's *label* colour, not its `border`. This is the one place the count badge
                // departs from `OrbitBadge`, and it is forced by where the two live. A status pill
                // sits on a card, where its own tint is enough to find it and the rim is a finishing
                // touch that can afford to be barely there. A count badge overlaps an icon, so its
                // translucent fill is by definition close to whatever is behind it and cannot be
                // what separates the two — the rim has to, which means the rim has to clear the 3:1
                // graphical floor of WCAG 1.4.11. The badge palette's `border` measures around
                // 2.1:1; its `label` was already tuned past 4.5:1 and clears it comfortably.
                edge = palette.label,
                edgeWidth = sizing.hairline,
            )
            // Horizontal only. The badge is already at its minimum height for a 12sp digit, and
            // vertical padding on top of that would make a two-digit count taller than a one-digit
            // one for no reason.
            .padding(horizontal = spacing.xs)
            .semantics { contentDescription = "$count $label" },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = shown,
            style = OrbitTheme.extendedTypography.metricCaption,
            color = palette.label,
            // The wrapper speaks the real count; this would announce the abbreviated one on top.
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}

/**
 * The same badge with no number: presence only.
 *
 * For places where something has changed but the count is either unknown or not worth the width — a
 * settings row with a new option under it, a tab with unseen content. A dot claims less than a
 * number does, which matters when the app does not actually know how much is behind it.
 *
 * This one stays solid where the numeric badge is glass. At 8dp there is no room for a fill, a rim
 * and a highlight to be separately legible — the three layers collapse into a single muddy tone —
 * and unlike the numeric badge it has no digit whose contrast has to be protected. A solid mark is
 * the only thing that still reads as deliberate at this size.
 */
@Composable
fun OrbitPresenceDot(
    label: String,
    modifier: Modifier = Modifier,
    tone: OrbitBadgeTone = OrbitBadgeTone.Red,
) {
    val palette = tone.colors
    Box(
        modifier = modifier
            .size(OrbitTheme.sizing.countBadgeDot)
            .orbitGlassShadow(shape = CircleShape, elevation = OrbitTheme.sizing.shadowBadge)
            .orbitGlass(
                fill = palette.solidContainer,
                shape = CircleShape,
                highlightAlpha = OrbitGlass.ButtonHighlightDark,
                sheen = 1f,
            )
            .semantics { contentDescription = label },
    )
}

/** Where the count stops being a number and becomes "a lot". */
const val MaxDisplayed = 99
