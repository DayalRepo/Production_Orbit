package com.orbitai.erp.core.designsystem.component.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The "add a new one" action pinned to the top of a dropdown.
 *
 * ### Why it is pinned rather than sitting at the end of the list
 *
 * Putting "Add stage" after the last option is the tidier-looking choice and it fails the moment the
 * list is long. The work sequence has around a hundred stages; a user who cannot find theirs has to
 * scroll to the bottom of a list they have already established does not contain what they want, just
 * to reach the control that fixes it. Pinned to the top, the escape hatch is visible at the exact
 * moment the search comes back empty.
 *
 * It is also the one row in the panel that does not scroll away, which is what makes it findable
 * without the user tracking where they are in the list.
 *
 * ### Accent, not neutral
 *
 * This is the only row in the panel that does something other than answer the field, and it opens a
 * dialog rather than selecting a value. Painting it in the action colour separates it from the
 * options beneath it, so a user scanning for a stage does not read it as one. The plus glyph carries
 * the same claim independently of colour.
 *
 * @param label the full phrase — "Add stage", "Add material". Naming the thing rather than saying
 *   "Add new" means the row still makes sense read on its own by a screen reader, which is how it
 *   will be encountered.
 */
@Composable
fun OrbitDropdownAddRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget)
            // A full-bleed row, so the ripple's own rectangular bounds are the right ones and no clip
            // is needed. It still has to be attached explicitly: a bare `clickable` takes whatever
            // `LocalIndication` happens to be, which is a no-op here, and a row that does not react
            // to a finger is the single loudest tell that a UI was built for a mouse.
            .indication(interactionSource, orbitPressIndication())
            .orbitHandCursor()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OrbitGlyph(
            icon = OrbitIcons.Add,
            size = sizing.iconSm,
            tint = content.iconPrimary,
            contentDescription = null,
            minimumStroke = sizing.iconStrokeLight,
        )
        Text(
            text = label,
            style = OrbitTheme.typography.bodyLarge,
            // A shade heavier than an option row. This is an action and they are values, and the
            // panel is easier to scan when the one control in it is not competing at the same
            // weight as the hundred things it sits above.
            fontWeight = FontWeight.SemiBold,
            color = content.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
