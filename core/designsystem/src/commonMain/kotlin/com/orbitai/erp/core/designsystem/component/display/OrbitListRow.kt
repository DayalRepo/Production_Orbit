package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.foundation.orbitHandCursor
import com.orbitai.erp.core.designsystem.foundation.orbitPressIndication
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.icon.OrbitIcons
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * One navigable entity in a list — task, issue, PO, inventory line, site update.
 *
 * Title + optional subtitle, with optional leading artwork and trailing chrome (badge, chevron).
 * Selection weight stays constant; trailing glyphs carry status, same rule as [OrbitDropdownRow].
 */
@Composable
fun OrbitListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors
    val interaction = remember { MutableInteractionSource() }
    val description = buildString {
        append(title)
        if (!subtitle.isNullOrBlank()) append(", ").append(subtitle)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = sizing.listRowMinHeight)
            .then(
                if (onClick != null) {
                    Modifier
                        .indication(interaction, orbitPressIndication())
                        .orbitHandCursor()
                        .clickable(
                            enabled = enabled,
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                } else {
                    Modifier
                },
            )
            .padding(horizontal = spacing.md, vertical = spacing.sm)
            .semantics(mergeDescendants = true) {
                contentDescription = description
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        leading?.invoke()

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = title,
                style = OrbitTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) content.textPrimary else content.textDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = OrbitTheme.typography.bodySmall,
                    color = if (enabled) content.textSecondary else content.textDisabled,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        trailing?.invoke()

        if (showChevron && onClick != null) {
            OrbitGlyph(
                icon = OrbitIcons.ArrowRight,
                size = sizing.iconSm,
                tint = content.iconInactive,
                contentDescription = null,
                minimumStroke = sizing.iconStrokeHairline,
                maximumStroke = sizing.iconStrokeHairline,
            )
        }
    }
}
