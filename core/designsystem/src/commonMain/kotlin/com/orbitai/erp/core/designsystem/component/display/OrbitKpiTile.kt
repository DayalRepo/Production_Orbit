package com.orbitai.erp.core.designsystem.component.display

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbitai.erp.core.designsystem.component.container.OrbitCard
import com.orbitai.erp.core.designsystem.foundation.orbitGlass
import com.orbitai.erp.core.designsystem.icon.OrbitGlyph
import com.orbitai.erp.core.designsystem.theme.OrbitBadgeTone
import com.orbitai.erp.core.designsystem.theme.OrbitGlass
import com.orbitai.erp.core.designsystem.theme.OrbitTheme
import com.orbitai.erp.core.designsystem.theme.colors

/**
 * Reusable glass KPI tile: title, optional icon chip, hero value, optional delta, footer slot.
 *
 * Layout mirrors common analytics cards (label → large figure → trend → visual/footer) while
 * staying on Orbit glass + shadow via [OrbitCard]. Domain metrics belong in `:shared` presets.
 */
@Composable
fun OrbitKpiTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTone: OrbitBadgeTone = OrbitBadgeTone.Blue,
    supporting: String? = null,
    delta: Float? = null,
    deltaHigherIsBetter: Boolean = true,
    deltaDescription: String = "",
    comparisonLabel: String? = null,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val spacing = OrbitTheme.spacing
    val content = OrbitTheme.contentColors
    val spoken = contentDescription ?: buildString {
        append(title)
        append(", ")
        append(value)
        if (supporting != null) {
            append(", ")
            append(supporting)
        }
        if (delta != null && deltaDescription.isNotBlank()) {
            append(", ")
            append(deltaDescription)
        }
    }

    OrbitCard(
        modifier = modifier.fillMaxWidth(),
        padding = spacing.md,
        onClick = onClick,
        contentDescription = spoken,
    ) {
        Column(modifier = Modifier.clearAndSetSemantics {}) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                if (icon != null) {
                    OrbitKpiIconChip(icon = icon, tone = iconTone)
                }
                Text(
                    text = title.uppercase(),
                    style = OrbitTheme.extendedTypography.cardLabel,
                    color = content.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = value,
                    style = OrbitTheme.extendedTypography.metricLarge,
                    color = content.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (delta != null) {
                    OrbitDelta(
                        value = delta,
                        higherIsBetter = deltaHigherIsBetter,
                        contentDescription = "",
                    )
                }
                if (comparisonLabel != null) {
                    Text(
                        text = comparisonLabel,
                        style = OrbitTheme.extendedTypography.metricCaption,
                        color = content.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (supporting != null) {
                Spacer(Modifier.height(spacing.xxs))
                Text(
                    text = supporting,
                    style = OrbitTheme.extendedTypography.metricCaption,
                    color = content.textTertiary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            if (footer != null) {
                Spacer(Modifier.height(spacing.md))
                footer()
            }
        }
    }
}

@Composable
fun OrbitKpiIconChip(
    icon: ImageVector,
    tone: OrbitBadgeTone,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    val sizing = OrbitTheme.sizing
    val palette = tone.colors
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .orbitGlass(
                fill = palette.container,
                shape = shape,
                highlightAlpha = if (OrbitTheme.isDark) {
                    OrbitGlass.BadgeHighlightDark
                } else {
                    OrbitGlass.BadgeHighlightLight
                },
                edge = palette.border,
                edgeWidth = sizing.hairline,
            ),
        contentAlignment = Alignment.Center,
    ) {
        OrbitGlyph(
            icon = icon,
            size = sizing.iconSm,
            tint = palette.icon,
            minimumStroke = sizing.iconStrokeLight,
            contentDescription = null,
        )
    }
}
