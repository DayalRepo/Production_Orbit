package com.orbitai.erp.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.theme.OrbitTheme

/**
 * Icon + uppercase heading on the left, value on the right.
 *
 * Shared meta row for glass cards — work items (Remaining, Where, Severity, Assigned) and
 * unit cards (Initiated & Target, Expected delay, Issues, Assigned).
 */
@Composable
fun WorkItemMetaRow(
    icon: ImageVector,
    heading: String,
    modifier: Modifier = Modifier,
    value: @Composable RowScope.() -> Unit,
) {
    val spacing = OrbitTheme.spacing
    val sizing = OrbitTheme.sizing
    val content = OrbitTheme.contentColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = sizing.minTouchTarget),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OrbitGlyph(
            icon = icon,
            size = sizing.iconSm,
            tint = content.iconInactive,
            contentDescription = null,
        )
        Text(
            text = heading.uppercase(),
            style = OrbitTheme.extendedTypography.cardLabel,
            color = content.textTertiary,
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = value,
        )
    }
}

@Composable
fun WorkItemMetaRow(
    icon: ImageVector,
    heading: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val content = OrbitTheme.contentColors
    WorkItemMetaRow(
        icon = icon,
        heading = heading,
        modifier = modifier,
    ) {
        Text(
            text = value,
            style = OrbitTheme.typography.bodyMedium.copy(
                fontWeight = OrbitTheme.fontWeights.title,
            ),
            color = content.textPrimary,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
