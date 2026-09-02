package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitAlpha
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors

/**
 * One option in an open dropdown.
 *
 * ### Selection is a green tick on the far side, and nothing else
 *
 * The row does not get heavier, larger or brighter when it is the current answer. That was the first
 * attempt and it was wrong for a specific reason: bolding a row changes its text metrics, so in a
 * scrolling list of a hundred stages the rows visibly reflow as the selection moves, and the one row
 * the user is trying to read is the one that just changed shape under them. Worse, weight is the
 * same signal this system uses for "this is the important value" elsewhere, so a bold row reads as a
 * recommendation rather than as a record of what you picked.
 *
 * A tick in the trailing gutter costs no layout change at all. It sits at the opposite end from the
 * label, which is where the eye ends up after reading the row anyway, and green means the same thing
 * here as it does on the copy button and every other affirmative in the product. Because it is a
 * glyph and not a colour wash, it survives greyscale — the tick is still a tick.
 *
 * ### Every label is full-strength ink
 *
 * Options are content, not chrome, and there is no reason for an unchosen stage to be quieter than a
 * chosen one — the user is reading the list precisely because they have not decided yet. Only the
 * genuinely unavailable rows are dimmed, and they are dimmed because they are unavailable rather
 * than because they are unselected.
 *
 * @param enabled false for an option already taken in a multi-select list. It stays visible rather
 *   than being removed: a list whose contents change as you pick from it loses the position the user
 *   was scrolled to and, worse, makes them doubt whether the option they chose was the one they
 *   meant. Dimmed and inert says "you have this one" without moving anything.
 */
@Composable
fun OrbitDropdownRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(
            text = label,
            // The platform's own body size, from the type scale, so a stage name in this list is set
            // exactly as a line of content is set anywhere else on the platform.
            style = OrbitTheme.typography.bodyLarge,
            // Constant across every row, selected or not. See above for why the selected row does
            // not get heavier.
            fontWeight = FontWeight.Medium,
            color = if (enabled) content.textPrimary else content.textDisabled,
            // Stage and unit names wrap rather than ellipsize — the full string is what gets ordered.
            maxLines = Int.MAX_VALUE,
            modifier = Modifier
                .weight(1f)
                // Selection and unavailability are carried by a glyph and by dimming; the dimming
                // does not survive being read aloud, so both are stated.
                .semantics {
                    contentDescription = when {
                        !enabled -> "$label, already selected"
                        selected -> "$label, selected"
                        else -> label
                    }
                },
        )

        if (selected) {
            OrbitGlyph(
                icon = OrbitIcons.Tick,
                size = sizing.iconSm,
                tint = if (enabled) content.iconPrimary else content.iconPrimary.copy(alpha = OrbitAlpha.Disabled),
                contentDescription = null,
                minimumStroke = sizing.iconStrokeLight,
            )
        }
    }
}
