package com.orbitai.erp.core.designsystem.component.datetime

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldShell
import com.orbitai.erp.core.designsystem.component.input.OrbitFieldState
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * The field that displays a chosen date and time and opens a picker when tapped.
 *
 * ### Read-only on purpose
 *
 * There is no text cursor and no keyboard. A typed date field has to parse free text, decide what
 * `12/13/2025` means, and reject half of what a user types — and it can produce a date in the past,
 * which the picker cannot. Making the field a button that displays a value moves the entire validation
 * problem into a control where invalid states are unreachable rather than caught.
 *
 * It still uses [OrbitFieldShell], so it inherits the rim, the focus ring, the height and the disabled
 * treatment of every other field on the form. A date input that looked like a button would read as an
 * action rather than as one of the form's values.
 *
 * @param value the formatted selection, or null for the empty state. Pass
 *   `selection?.format()` for a single moment or `range.format()` for a span; both include the
 *   weekday name and `dd/MM/yyyy`, with time when applicable.
 * @param placeholder shown when [value] is null. Should name what is being picked — "Target date"
 *   rather than "Select" — because the field is the only label many forms give it.
 */
@Composable
fun OrbitDateTimeField(
    value: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    state: OrbitFieldState = OrbitFieldState.Default,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors

    // Its own source rather than a shared one: the shell animates its rim from this, and a field that
    // borrowed the picker's interactions would light up while the panel below it was being used.
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = OrbitTheme.extendedTypography.cardLabel,
                color = content.textSecondary,
            )
            Box(modifier = Modifier.height(spacing.xxs))
        }

        OrbitFieldShell(
            interactionSource = interactionSource,
            shape = OrbitTheme.shapeTokens.field,
            minHeight = sizing.fieldHeightMd,
            horizontalPadding = sizing.fieldPaddingMd,
            enabled = enabled,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (enabled) {
                        Modifier
                            .orbitHandCursor()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                role = Role.Button,
                                onClick = onClick,
                            )
                    } else {
                        Modifier
                    },
                ),
        ) {
            OrbitGlyph(
                // The grid-faced calendar rather than the generic one. At 16dp inside a field its
                // ruled squares still read as a calendar, where the generic glyph's finer detail
                // collapses into a box with a smudge in it.
                icon = OrbitIcons.CalendarDate,
                size = sizing.iconSm,
                tint = if (enabled) content.iconInactive else content.iconDisabled,
                contentDescription = null,
            )

            Text(
                text = value ?: placeholder,
                style = OrbitTheme.typography.bodyLarge,
                // The value carries weight and the placeholder does not. This is the field's only cue
                // that it holds something, since a read-only field has no cursor to show a difference.
                fontWeight = if (value != null) FontWeight.Medium else FontWeight.Normal,
                color = when {
                    !enabled -> content.textDisabled
                    value != null -> content.textPrimary
                    else -> content.textTertiary
                },
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
